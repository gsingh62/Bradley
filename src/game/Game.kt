package game

import action.Action
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
                when (player.chooseNextMove()) {
                    Action.MOVE_NORTH -> moveNorthLogic()
                    Action.MOVE_NORTHWEST -> moveNorthWestLogic()
                    Action.MOVE_WEST -> moveWestLogic()
                    Action.MOVE_SOUTHWEST -> moveSouthWestLogic()
                    Action.MOVE_SOUTH -> moveSouthLogic()
                    Action.MOVE_SOUTHEAST -> moveSouthEastLogic()
                    Action.MOVE_EAST -> moveEastLogic()
                    Action.MOVE_NORTHEAST -> moveNorthEastLogic()
                }
                timeCounter++
            } catch (ex: InvalidMoveException) {
                player.receiveFeedback(ex)
            }
        }
    }

    private fun moveNorthEastLogic() {
        worldMap.moveObject(player.actor, 1, -1)
    }

    private fun moveEastLogic() {
        worldMap.moveObject(player.actor, 1, 0)
    }

    private fun moveSouthEastLogic() {
        worldMap.moveObject(player.actor, 1, 1)
    }

    private fun moveSouthLogic() {
        worldMap.moveObject(player.actor, 0, 1)
    }

    private fun moveSouthWestLogic() {
        worldMap.moveObject(player.actor, -1, 1)
    }

    private fun moveWestLogic() {
        worldMap.moveObject(player.actor, -1, 0)
    }

    private fun moveNorthWestLogic() {
        worldMap.moveObject(player.actor, -1, -1)
    }

    private fun moveNorthLogic() {
        worldMap.moveObject(player.actor, 0, -1)
    }
}