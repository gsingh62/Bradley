package game

import action.Move
import agent.Player
import exception.InvalidMoveException
import exception.InvalidVectorException
import map.ExitNode
import map.WorldMap

class Game(private val player: Player,
           private val worldMap: WorldMap,
           private val rules: List<(Move) -> InvalidMoveException?> = listOf(preventTeleportation)) {

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
                        println("actor position: " + worldMap.positionFor(player.actor))
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
        rules.forEach { it(move)?.let{ throw it } }
    }
}

private val preventTeleportation: (Move) -> InvalidMoveException? =
        { m: Move ->
            if (!(Math.abs(m.vector.deltax) <= 1 && Math.abs(m.vector.deltay) <= 1))
                InvalidVectorException() else null
        }


