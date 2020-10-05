package functional

import action.Action
import agent.GeneralPlayer
import agent.Player
import exception.HitWallException
import exception.InvalidMoveException
import game.Game
import map.Actor
import map.Coordinate
import map.WorldMapBuilder
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
}

class AlwaysGoUpPlayer(override val actor: Actor): Player {
    override var feedback: InvalidMoveException? = null

    override fun chooseNextMove(): Action {
        return Action.MOVE_NORTH
    }
}

