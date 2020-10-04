package game

import action.Action
import agent.Player
import map.ExitNode
import map.WorldMap
import java.lang.UnsupportedOperationException

class Game(private val player: Player, private val worldMap: WorldMap) {

    var timeCounter: Int = 0

    fun run() {
        while(!Thread.interrupted()) {
            val nextMove = player.chooseNextMove()
            when (nextMove) {
                Action.MOVE_NORTH ->
                    worldMap.moveObject(player.actor, 0, -1)
                else -> {
                    throw UnsupportedOperationException()
                }
            }

            if (!player.actor.alive || worldMap.getNode(worldMap.positionFor(player.actor)) is ExitNode) {
                break
            }

            timeCounter++
        }
    }

}