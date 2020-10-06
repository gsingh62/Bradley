package game

import action.Move
import agent.Player
import exception.InvalidMoveException
import map.ExitNode
import map.WorldMap

class Game(private val player: Player,
           private val worldMap: WorldMap,
           private val gameRules: List<(Move) -> InvalidMoveException?>) {

    private var timeCounter: Int = 0

    fun run() {
        while (!Thread.interrupted()) {
            try {
                if (!player.actor.alive || worldMap.getNode(worldMap.positionFor(player.actor)) is ExitNode) {
                    break
                }
                player.actor.loseOneLifeUnit()
                when (val action = player.chooseNextMove()) {
                    is Move ->  {
                        validateRules(action)
                        worldMap.moveObject(player.actor, action.vector)
                    }
                }
                timeCounter++
            } catch (ex: InvalidMoveException) {
                player.receiveFeedback(ex)
            }
        }
    }

    private fun validateRules(move: Move) {
        gameRules.forEach { it(move)?.let{ throw it } }
    }
}