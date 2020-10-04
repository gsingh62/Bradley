package game

import action.Action
import agent.Player
import map.AlwaysGoUpPlayer
import map.ExitNode
import map.WorldMap
import java.lang.UnsupportedOperationException

class Game(private val player: Player, private val worldMap: WorldMap) {
    companion object {
        const val WALL_FEEDBACK = "You cannot go forward, as you have hit a wall."
    }

    var timeCounter: Int = 0

    fun run() {
        while(!Thread.interrupted()) {
            when (player.chooseNextMove()) {
                Action.MOVE_NORTH -> moveNorthLogic()
            }
            if (!player.actor.alive ||
                    player.feedback == WALL_FEEDBACK ||
                    worldMap.getNode(worldMap.positionFor(player.actor)) is ExitNode) {
                break
            }
            timeCounter++
        }
    }

    private fun moveNorthLogic() {
        try {
            worldMap.moveObject(player.actor, 0, -1)
        } catch (ex: UnsupportedOperationException) {
            when (ex.message) {
                "Hit wall node." -> if (player is AlwaysGoUpPlayer) { player.receiveFeedback(WALL_FEEDBACK) }
            }
        }
    }
}