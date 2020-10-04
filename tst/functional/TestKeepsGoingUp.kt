package functional

import action.Action
import agent.Player
import exception.HitWallException
import game.Game
import map.Actor
import map.Coordinate
import map.WorldMapBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertNotEquals
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
        assertThat(player.feedback, equalTo(HitWallException.HIT_WALL_EXCEPTION_MESSAGE))
    }
}

class AlwaysGoUpPlayer(override val actor: Actor): Player {
    override lateinit var feedback: String

    override fun chooseNextMove(): Action {
        return Action.MOVE_NORTH
    }
}

