package game

import action.Move
import agent.Player
import exception.InvalidMoveException
import exception.InvalidVectorException
import map.ExitNode
import map.WorldMap
import org.slf4j.LoggerFactory

class Game(private val player: Player,
           private val worldMap: WorldMap) {

    private val log = LoggerFactory.getLogger(Game::class.java)

    private var timeCounter: Int = 0

    fun run() {
        while (!Thread.interrupted()) {
            try {
                if (!player.actor.alive || worldMap.getNode(worldMap.positionFor(player.actor)) is ExitNode) {
                    break
                }
                player.actor.loseOneLifeUnit()
                player.actor.setSurrounding(worldMap.getSurrounding(player.actor))
                when (val action = player.chooseNextMove()) {
                    is Move ->  {
                        validateRules(action)
                        worldMap.moveObject(player.actor, action.vector)
                        log.info("moved actor to {}", worldMap.positionFor(player.actor))
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


val preventCrossingBoundaries: (Move) -> InvalidMoveException? =
        { m: Move ->
            if (!( Math.abs(m.vector.deltax) >= 0 && Math.abs(m.vector.deltax) >= 0 &&
                            Math.abs(m.vector.deltax) <= 8 && Math.abs(m.vector.deltay) <= 5))
                InvalidVectorException() else null
        }

private val rules = listOf(preventCrossingBoundaries, preventTeleportation)


