package functional

import action.Action
import agent.Player
import game.Game
import map.Actor
import map.Coordinate
import map.WorldMapBuilder
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.Test

class TestEmptyMapBradleyKeepsGoingUpAndWins {

    @Test
    fun test() {
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

}

class AlwaysGoUpPlayer(override val actor: Actor): Player {
    override fun chooseNextMove(): Action {
        return Action.MOVE_NORTH
    }
}
