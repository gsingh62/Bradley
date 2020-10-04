package game

import action.Action
import agent.Player
import exception.ExceptionMessages.Companion.HIT_WALL_EXCEPTION_MESSAGE
import exception.ExceptionMessages.Companion.WALL_FEEDBACK
import exception.InvalidMoveException
import map.ExitNode
import map.WorldMap

class Game(private val player: Player, private val worldMap: WorldMap) {

    var timeCounter: Int = 0

    fun run() {
        while(!Thread.interrupted()) {
            when (player.chooseNextMove()) {
                Action.MOVE_NORTH -> moveNorthLogic()
            }
            if (!player.actor.alive || worldMap.getNode(worldMap.positionFor(player.actor)) is ExitNode) {
                break
            }
            timeCounter++
        }
    }

    private fun moveNorthLogic() {
        try {
            worldMap.moveObject(player.actor, 0, -1)
        } catch (ex: InvalidMoveException) {
            when (ex.message) {
                HIT_WALL_EXCEPTION_MESSAGE -> { player.receiveFeedback(WALL_FEEDBACK) }
            }
        }
    }
}