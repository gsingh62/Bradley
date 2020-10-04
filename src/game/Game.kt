package game

import action.Action
import agent.Player
import exception.InvalidMoveException
import map.ExitNode
import map.WorldMap

class Game(private val player: Player, private val worldMap: WorldMap) {

    private var timeCounter: Int = 0

    fun run() {
        try {
            while (!Thread.interrupted()) {
                player.actor.loseOneLifeUnit()
                when (player.chooseNextMove()) {
                    Action.MOVE_NORTH -> moveNorthLogic()
                }
                if (!player.actor.alive || worldMap.getNode(worldMap.positionFor(player.actor)) is ExitNode) {
                    break
                }
                timeCounter++
            }
        } catch (ex: InvalidMoveException) {
            player.receiveFeedback(ex.message!!)
        }
    }

    private fun moveNorthLogic() {
        worldMap.moveObject(player.actor, 0, -1)
    }
}