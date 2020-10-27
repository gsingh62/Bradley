package functional

import action.Action
import action.Move
import agent.GeneralPlayer
import agent.Player
import exception.HitWallException
import exception.InvalidMoveException
import exception.InvalidVectorException
import game.Game
import map.Actor
import map.Coordinate
import map.ExitNode
import map.Memory
import map.Node
import map.OpenSpaceNode
import map.Vector
import map.WorldMap
import map.WorldMapBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.abs

class TestKeepsGoingUp {

    @Test
    fun testWinsWithNoWalls() {
        val builder = WorldMapBuilder()
                .load("""
                    .e.
                    ...
                    .s.
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = AlwaysGoUpPlayer(actor)
        val game = Game(player, map)

        game.run()
        assertThat(map.positionFor(actor), equalTo(exitPosition))
        assertThat(actor.alive, equalTo(true))
    }

    @Test
    fun testDoesntWinWithWalls() {
        val builder = WorldMapBuilder()
                .load("""
                    .e.
                    XXX
                    .s.
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = AlwaysGoUpPlayer(actor)
        val game = Game(player, map)
        game.run()

        assertNotEquals(map.positionFor(actor), exitPosition)
        assertTrue(player.feedback is HitWallException)
    }

    @Test
    fun testNavigatesWallAndWins() {
        val builder = WorldMapBuilder()
                .load("""
                    .e.
                    .X.
                    .s.
                """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = GeneralPlayer(actor)
        val game = Game(player, map)
        game.run()

        assertEquals(exitPosition, map.positionFor(actor))
        assertEquals(true, actor.alive)
    }

    @Test
    fun testDoesntWinWithGameRulesBeingViolated() {
        val builder = WorldMapBuilder()
                .load("""
                    .e.
                    ...
                    .s.
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = TeleportingPlayer(actor)
        val game = Game(player, map)
        game.run()

        assertNotEquals(map.positionFor(actor), exitPosition)
        assertTrue(player.feedback is InvalidVectorException)
    }

    @Test
    fun testBFS() {
        val builder = WorldMapBuilder()
                .load("""
                    .........
                    ...X.X...
                    ...XeX...
                    ...XXX...
                    .........
                    .........
                    ....s....
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val player = BFSPlayerGayatri(actor)
        player.setWorldMap(map)
        val game = Game(player, map)
        game.run()

        assertEquals(exitPosition, map.positionFor(actor))
        assertEquals(true, actor.alive)
    }

    @Test
    fun testExploratoryPlayerWithEndNode() {
        val builder = WorldMapBuilder()
                .load("""
                    .....X...
                    ....eX...
                    ..XXXX...
                    .XX......
                    .X.......
                    .X.......
                    .X......s
                    .........
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val memory = Memory.init(map.getAllCoordinates())

        val player = ExploratoryPlayerWithEndNode(actor, memory, exitPosition)
        val game = Game(player, map)
        game.run()

        assertEquals(exitPosition, map.positionFor(actor))
        assertEquals(true, actor.alive)
    }
}

class AlwaysGoUpPlayer(override val actor: Actor): Player {
    override var feedback: InvalidMoveException? = null

    override fun chooseNextMove(): Action {
        return Move(Vector(0,-1))
    }
}

class TeleportingPlayer(override val actor: Actor): Player {
    override var feedback: InvalidMoveException? = null

    override fun chooseNextMove(): Action {
        return Move(Vector(0,-50))
    }
}

/*class ExploratoryPlayerWithoutEndNode(override val actor: Actor) : Player {
    override var feedback: InvalidMoveException? = null

    override fun chooseNextMove(): Action {

    }*/

class ExploratoryPlayerWithEndNode(override val actor: Actor, private val memory: Memory,
                                   private val end: Coordinate): Player {
    override var feedback: InvalidMoveException? = null
    override fun chooseNextMove(): Action {
        val surrounding: WorldMap = actor.getSurrounding()
        memory.furnishMemoryWithSurrounding(surrounding)
        val start = surrounding.positionFor(actor)

        val distances = HashMap<Coordinate, Int>()
        val queue  = LinkedList<Coordinate>()
        queue.add(start)
        distances[start] = 0
        while(queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            val currDistance = distances[curr] ?: throw IllegalStateException()
            for (neighbour in neighboursOf(curr)) {
                if (distances[neighbour] == null) {
                    distances[neighbour] = currDistance + 1
                    queue.add(neighbour)

                    if (end == neighbour) {
                        break
                    }
                }
            }
        }

        var curr = end
        while (distances[curr] != 1) {
            val currDistance = distances[curr] ?: throw IllegalStateException()
            for (neighbour in neighboursOf(curr)) {
                val neighbourDist = distances[neighbour] ?: throw IllegalStateException()
                if (neighbourDist == currDistance-1) {
                    curr = neighbour
                    break
                }
            }
        }
        return Move(Vector(curr.x - start.x, curr.y - start.y))
    }

    private fun neighboursOf(coordinate: Coordinate): Set<Coordinate> {
        val neighbours = HashSet<Coordinate>()
        for (potential in memory.getAllCoordinates()) {
            if (memory.getNode(potential) is OpenSpaceNode) {
                if (abs(potential.x - coordinate.x) <= 1 &&
                        abs(potential.y - coordinate.y) <= 1) {
                    neighbours.add(potential)
                }
            }
        }
        return neighbours
    }
}
/**
 * This was basically copied over from BFSPlayerAlexey to try to understand
 * how to code it up.
 */
class BFSPlayerGayatri(override val actor: Actor): Player {
    override var feedback: InvalidMoveException? = null
    private lateinit var worldMap: WorldMap

    fun setWorldMap(worldMap: WorldMap) {
        this.worldMap = worldMap
    }

    override fun chooseNextMove(): Action {
        val start = worldMap.positionFor(actor)
        val end = getEndPosition()

        val distances = HashMap<Coordinate, Int>()
        val queue  = LinkedList<Coordinate>()
        queue.add(start)
        distances[start] = 0
        while(queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            val currDistance = distances[curr] ?: throw IllegalStateException()
            for (neighbour in neighboursOf(curr)) {
                if (distances[neighbour] == null) {
                    distances[neighbour] = currDistance + 1
                    queue.add(neighbour)

                    if (end == neighbour) {
                        break
                    }
                }
            }
        }
        var curr = end
        while (distances[curr] != 1) {
            val currDistance = distances[curr] ?: throw IllegalStateException()
            for (neighbour in neighboursOf(curr)) {
                val neighbourDist = distances[neighbour] ?: throw IllegalStateException()
                if (neighbourDist == currDistance-1) {
                    curr = neighbour
                    break
                }
            }
        }
        return Move(Vector(curr.x - start.x, curr.y - start.y))
    }

    fun getEndPosition(): Coordinate {
        for(coordinate in worldMap.getAllCoordinates()) {
            if (worldMap.getNode(coordinate) is ExitNode)
                return coordinate
        }
        throw java.lang.IllegalStateException("There is no exit node")
    }
    fun neighboursOf(coordinate: Coordinate): Set<Coordinate> {
        val neighbours = HashSet<Coordinate>()
        for (potential in worldMap.getAllCoordinates()) {
            if (worldMap.getNode(potential) is OpenSpaceNode) {
                if (Math.abs(potential.x - coordinate.x) <= 1 &&
                        Math.abs(potential.y - coordinate.y) <= 1) {
                    neighbours.add(potential)
                }
            }
        }
        return neighbours
    }

}

class BFSPlayerAlexey(override val actor: Actor): Player {
    override var feedback: InvalidMoveException? = null
    private lateinit var worldMap: WorldMap

    fun setWorldMap(worldMap: WorldMap) {
        this.worldMap = worldMap
    }
    override fun chooseNextMove(): Action {
        val srcPos = worldMap.positionFor(actor)
        val dstPos = findExit(worldMap)

        val distanceMap = HashMap<Coordinate, Int>()
        val queue = LinkedList<Coordinate>()
        distanceMap[srcPos] = 0
        queue.add(srcPos)

        while (queue.isNotEmpty()) {
            val cur = queue.removeFirst()
            val curDistance = distanceMap[cur] ?: throw IllegalStateException()

            for (neighbour in openNeighboursOf(cur, worldMap)) {
                if (distanceMap[neighbour] == null) {
                    distanceMap[neighbour] = curDistance+1
                    queue.add(neighbour)

                    if (neighbour == dstPos) {
                        break
                    }
                }
            }
        }

        var cur = dstPos
        while (distanceMap[cur] != 1) {
            val curDist = distanceMap[cur] ?: throw IllegalStateException()
            for (neighbour in openNeighboursOf(cur, worldMap)) {
                val neighbourDist = distanceMap[neighbour] ?: continue
                if (neighbourDist == curDist-1) {
                    cur = neighbour
                    break
                }
            }
        }

        return Move(Vector(cur.x - srcPos.x, cur.y - srcPos.y))
    }

    private fun openNeighboursOf(cur: Coordinate, worldMap: WorldMap): Set<Coordinate> {
        val neighbours = HashSet<Coordinate>()

        for (candidate in worldMap.getAllCoordinates()) {
            if (worldMap.getNode(candidate) is OpenSpaceNode) {
                if (Math.abs(candidate.x-cur.x)<=1 && Math.abs(candidate.y-cur.y)<=1) {
                    neighbours.add(candidate)
                }
            }
        }

        return neighbours
    }

    private fun findExit(worldMap: WorldMap): Coordinate {
        for (coord in worldMap.getAllCoordinates()) {
            val node = worldMap.getNode(coord)
            if (node is ExitNode) {
                return coord
            }
        }

        throw IllegalStateException("no exit node on map")
    }
}

