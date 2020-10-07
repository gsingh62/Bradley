package game

import action.Move
import agent.Player
import exception.InvalidMoveException
import exception.InvalidVectorException
import map.ExitNode
import map.WorldMap

class Game(private val player: Player,
           private val worldMap: WorldMap,
           private val gameRules: List<(Move) -> InvalidMoveException?> = GameRules.rules) {

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

class GameRules{
    companion object {
        private val preventTeleportation: (Move) -> InvalidMoveException? =
                { m: Move ->
                    if (!(Math.abs(m.vector.deltax) <= 1 && Math.abs(m.vector.deltay) <= 1))  InvalidVectorException() else null
                }
        val rules = listOf(preventTeleportation)
    }
}