package functional

import action.Action
import action.Move
import agent.GeneralPlayer
import agent.Player
import exception.HitWallException
import exception.InvalidMoveException
import exception.InvalidVectorException
import game.Game
import map.*
import map.Vector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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
                    ....e....
                    .........
                    .........
                    .........
                    ....s....
                    """)
        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        val preventCrossingBoundaries: (Move) -> InvalidMoveException? =
                { m: Move ->
                    if (!( Math.abs(m.vector.deltax) >= 0 && Math.abs(m.vector.deltax) >= 0 &&
                                    Math.abs(m.vector.deltax) <= 8 && Math.abs(m.vector.deltay) <= 5))
                        InvalidVectorException() else null
                }

        val player = BFSPlayer(actor)
        player.setStartNodeCoordinate(map.positionFor(actor))
        val game = Game(player, map, listOf(preventCrossingBoundaries))
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

class BFSPlayer(override val actor: Actor): Player {
    override var feedback: InvalidMoveException? = null
    private lateinit var previousNodeCoordinate: Coordinate
    fun setStartNodeCoordinate(startNodeCoordinate: Coordinate) {
        println("x: "  + startNodeCoordinate.x + " y: " + startNodeCoordinate.y)
        actor.addNextNodeCoordinate(coordinate = startNodeCoordinate)
        previousNodeCoordinate = startNodeCoordinate
    }
    override fun chooseNextMove(): Action {
        if (feedback != null) {
            println(feedback)
        }
        if (!actor.isNodeQueueEmpty()) {
            val coordinate = actor.getNextNodeCoordinate()
            while (coordinate.isVisited()) {
                val coordinate = actor.getNextNodeCoordinate()
            }
            coordinate.markNodeVisited()

            for (coordinate in coordinate.getSurroundingNodes()) {
                    actor.addNextNodeCoordinate(coordinate)
            }
            val action = previousNodeCoordinate.calculateNewMoveVector(coordinate)
            println("x: "  + coordinate.x + " y: " + coordinate.y)

            return Move(action)
        }
        return Move(Vector(0,0))
    }
}

