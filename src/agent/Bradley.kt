package agent

import action.Action
import action.Move
import exception.InvalidMoveException
import map.Actor
import map.Coordinate
import map.ExitNode
import map.Node
import map.OpenSpaceNode
import map.Vector
import map.WorldMap
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface Player {
    val actor: Actor
    var feedback: InvalidMoveException?
    fun chooseNextMove(): Action
    fun receiveFeedback(feedback: InvalidMoveException) {
        this.feedback = feedback
    }
}

class GeneralPlayer(override val actor: Actor): Player {
    private val options = listOf(Vector(1,-1), Vector(1,0), Vector(1, 1),
    Vector(0,1),Vector(-1, 1), Vector(-1, 0), Vector(-1, -1),
    Vector(0, -1))
    override var feedback: InvalidMoveException? = null
    private var previousActionIndex = 0
    override fun chooseNextMove(): Action {
        if (feedback != null) {
            feedback = null
            val newActionIndex = (previousActionIndex + 1) % 8
            previousActionIndex = newActionIndex
        }
        return Move(options[previousActionIndex])
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

class SpiralCoordinateIterator(private val r: Double = 0.5 / Math.PI, private val d: Double = 1.0, private val start: Coordinate) {
    var n = 0
    fun getNextSpiralCoordinate(): Coordinate {
        val t = sqrt(2.0 * d * n++ / r)
        val rt = 2 * r * t

        val x =  rt * cos(t)
        val y =  rt * sin(t)
        return Coordinate(x.toInt() + start.x, y.toInt() + start.y)
    }
}

class SquareSpiralIterator(start: Coordinate) {
    var layer = 2
    var startX = start.x
    var startY = start.y
    private var previousCoordinate = start
    private var dir = Direction.RIGHT

    enum class Direction { RIGHT, LEFT, UP, DOWN }

    fun getNextSquareSpiralCoordinate(): Coordinate {
        var x = previousCoordinate.x
        var y = previousCoordinate.y
        when (dir) {
            Direction.RIGHT -> if (x == startX + layer) {
                dir = Direction.UP
            }
            Direction.UP -> if (y == startY - layer) {
                dir = Direction.LEFT
            }
            Direction.LEFT -> if (x == startX - layer) {
                layer+=3
                dir = Direction.DOWN
            }
            Direction.DOWN -> if (y == layer + startY) {
                dir = Direction.RIGHT
            }
        }
        when (dir) {
            Direction.RIGHT -> x++
            Direction.UP -> y--
            Direction.LEFT -> x--
            Direction.DOWN -> y++
        }
        previousCoordinate = Coordinate(x, y)
        return previousCoordinate
    }
}

class BeatAlexey368(override val actor: Actor, private val memory: Memory, private var startCoordinate: Coordinate): Player {
    override var feedback: InvalidMoveException? = null
    private var endCoordinate = startCoordinate
    private val spiralCoordinateIterator = SquareSpiralIterator(start = startCoordinate)
    private var previousEndCoordinateInaccessible = false
    override fun chooseNextMove(): Action {
        val surrounding = actor.getSurrounding()
        memory.furnishMemoryWithSurrounding(surrounding)
        startCoordinate = surrounding.positionFor(actor)
        while(true) {
            if (previousEndCoordinateInaccessible ||
                    endCoordinate == startCoordinate ||
                    memory.getNode(endCoordinate) !is OpenSpaceNode
            ) {
                endCoordinate = getNextCoordinate(surrounding)
                previousEndCoordinateInaccessible = false
            }
            return try {
                println ( "Start Coordinate: $startCoordinate, End Coordinate: $endCoordinate" )
                bfsToEndNode(startCoordinate, endCoordinate)
            } catch (ex: IllegalStateException) {
                if (memory.getNode(endCoordinate) is ExitNode) {
                    throw IllegalStateException("ExitNode is inaccessible")
                } else {
                    previousEndCoordinateInaccessible = true
                    println ( "Start Coordinate: $startCoordinate, End Coordinate: $endCoordinate" )
                    continue
                }
            }
        }
    }

    private fun getNextCoordinate(surrounding: WorldMap): Coordinate {
        for(coordinate in surrounding.getAllCoordinates()) {
            if (surrounding.getNode(coordinate) is ExitNode)
                return coordinate
        }
        while(true) {
            val nextCoordinate = spiralCoordinateIterator.getNextSquareSpiralCoordinate()
            if (!memory.isNodeVisited(nextCoordinate) &&
                    memory.getAllCoordinates().contains(nextCoordinate) &&
                    nextCoordinate != startCoordinate &&
                    memory.getNode(nextCoordinate) is OpenSpaceNode) {
                return nextCoordinate
            }
        }
    }

    private fun bfsToEndNode(start: Coordinate, end: Coordinate): Action {
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
        memory.markNodeVisited(curr)
        return Move(Vector(curr.x - start.x, curr.y - start.y))
    }

    private fun neighboursOf(coordinate: Coordinate): Set<Coordinate> {
        val neighbours = HashSet<Coordinate>()
        for (potential in memory.getAllCoordinates()) {
            if (memory.getNode(potential) is OpenSpaceNode) {
                if (abs(potential.x - coordinate.x) <= 1 &&
                        abs(potential.y - coordinate.y) <= 1 && potential != coordinate) {
                    neighbours.add(potential)
                }
            }
        }
        return neighbours
    }
}

class ExploratoryPlayerWithoutEndNode(override val actor: Actor, private val memory: Memory, private var startCoordinate: Coordinate): Player {

    override var feedback: InvalidMoveException? = null
    private var endCoordinate = startCoordinate
    private val spiralCoordinateIterator = SpiralCoordinateIterator(start = startCoordinate)
    private var previousEndCoordinateInaccessible = false
    override fun chooseNextMove(): Action {
        val surrounding = actor.getSurrounding()
        memory.furnishMemoryWithSurrounding(surrounding)
        startCoordinate = surrounding.positionFor(actor)
        while(true) {
            if (previousEndCoordinateInaccessible || endCoordinate == startCoordinate || memory.getNode(endCoordinate) !is OpenSpaceNode) {
                endCoordinate = getNextCoordinate(surrounding)
                previousEndCoordinateInaccessible = false
            }
            return try {
                bfsToEndNode(startCoordinate, endCoordinate)
            } catch (ex: IllegalStateException) {
                if (memory.getNode(endCoordinate) is ExitNode) {
                    throw IllegalStateException("ExitNode is inaccessible")
                } else {
                    previousEndCoordinateInaccessible = true
                    continue
                }
            }
        }
    }

    private fun getNextCoordinate(surrounding: WorldMap): Coordinate {
        for(coordinate in surrounding.getAllCoordinates()) {
            if (surrounding.getNode(coordinate) is ExitNode)
                return coordinate
        }
        while(true) {
            val nextCoordinate = spiralCoordinateIterator.getNextSpiralCoordinate()
            if (!memory.isNodeVisited(nextCoordinate) &&
                    memory.getAllCoordinates().contains(nextCoordinate) &&
                    nextCoordinate != startCoordinate &&
                    memory.getNode(nextCoordinate) is OpenSpaceNode) {
                return nextCoordinate
            }
        }
    }

    private fun bfsToEndNode(start: Coordinate, end: Coordinate): Action {
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
        memory.markNodeVisited(curr)
        return Move(Vector(curr.x - start.x, curr.y - start.y))
    }

    private fun neighboursOf(coordinate: Coordinate): Set<Coordinate> {
        val neighbours = HashSet<Coordinate>()
        for (potential in memory.getAllCoordinates()) {
            if (memory.getNode(potential) is OpenSpaceNode) {
                if (abs(potential.x - coordinate.x) <= 1 &&
                        abs(potential.y - coordinate.y) <= 1 && potential != coordinate) {
                    neighbours.add(potential)
                }
            }
        }
        return neighbours
    }
}

class ExploratoryPlayerWithEndNode(
        override val actor: Actor,
        private val memory: Memory,
        private val end: Coordinate
): Player {
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

private fun fillWithOpenNodes(coordinates: Set<Coordinate>): MutableMap<Coordinate, Node> {
    val nodes = HashMap<Coordinate, Node>()
    for(coordinate in coordinates) {
        nodes[coordinate] = OpenSpaceNode()
    }
    return nodes
}

class Memory(private val nodes: MutableMap<Coordinate, Node>) {
    val visitedNodes = HashSet<Coordinate>()
    constructor(coordinates: Set<Coordinate>) : this(fillWithOpenNodes(coordinates))

    fun furnishMemoryWithSurrounding(surrounding: WorldMap) {
        for (coordinate in surrounding.getAllCoordinates()) {
            nodes[coordinate] = surrounding.getNode(coordinate)
        }
    }

    fun getNode(coordinate: Coordinate): Node {
        return nodes[coordinate] ?: throw IllegalStateException("Node not found in this position")
    }

    fun getAllCoordinates(): Set<Coordinate> = nodes.keys

    fun markNodeVisited(node: Coordinate) {
        visitedNodes.add(node)
    }

    fun isNodeVisited(node: Coordinate): Boolean {
        return visitedNodes.contains(node)
    }
}

class Bradley {

//    val api: WorldApiForAgents
//    val actor: Actor
    
//    val memory: Memory = Memory()
//    val actionControl: ActionControl = ActionControl()
//    val learningUnit: LearningUnit = LearningUnit(memory, actionControl)
//    val visualProcessingUnit: VisualProcessingUnit = VisualProcessingUnit()

/*
    fun chooseNextMove(): Action {
        visualProcessingUnit.see(getVicinity(), learningUnit)
        currentNode = learningUnit.takeAction()
        
    }
*/

/*    private fun getVicinity(): VisualStimuli {
        // TODO how to enforce Bradley not being able to observe arbitrary position?
        return worldApi.getVicinity(actor)
    }

    fun currentPosition(): Coordinate {
        return gameEngine.positionFor(actor)
    }*/
}
