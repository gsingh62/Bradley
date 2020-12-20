package game

import action.Move
import agent.Player
import exception.InvalidMoveException
import map.Coordinate
import map.ExitNode
import map.WorldMap
import output.SendGameData

class Game(
        private val player: Player,
        private val worldMap: WorldMap,
        private val radius: Int = 2
) {
    private var timeCounter: Int = 0

    fun run() {
        val turns = ArrayList<Coordinate>()
        while (!Thread.interrupted()) {
            try {
                val presentCoordinate = worldMap.positionFor(player.actor)
                turns.add(presentCoordinate)
                if (!player.actor.alive || worldMap.getNode(presentCoordinate) is ExitNode) {
                    break
                }
                player.actor.loseOneLifeUnit()
                player.actor.setSurrounding(worldMap.getSurrounding(player.actor, radius))
                when (val action = player.chooseNextMove()) {
                    is Move ->  {
                        validateRules(action, presentCoordinate)
                        worldMap.moveObject(player.actor, action.vector)
                    }
                }
                timeCounter++
            } catch (ex: InvalidMoveException) {
                player.receiveFeedback(ex)
            }
        }
        SendGameData().sendGameData(worldMap, radius, turns)
    }

    private fun validateRules(move: Move, presentCoordinate: Coordinate) {
        worldMap.getWorldRules().worldRulesList.forEach { it(move, presentCoordinate)?.let{ throw it } }
    }
}