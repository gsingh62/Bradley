package game

import action.Move
import agent.Player
import exception.InvalidMoveException
import map.ExitNode
import map.WorldMap

class Game(private val player: Player, private val worldMap: WorldMap) {

    private var timeCounter: Int = 0

    fun run() {
        while (!Thread.interrupted()) {
            try {
                if (!player.actor.alive || worldMap.getNode(worldMap.positionFor(player.actor)) is ExitNode) {
                    break
                }
                player.actor.loseOneLifeUnit()
                worldMap.moveObject(player.actor, (player.chooseNextMove() as Move).vector)
                timeCounter++
            } catch (ex: InvalidMoveException) {
                player.receiveFeedback(ex)
            }
        }
    }
}