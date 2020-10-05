package agent

import action.Action
import exception.InvalidMoveException
import map.Actor

interface Player {
    val actor: Actor
    var feedback: InvalidMoveException?
    fun chooseNextMove(): Action
    fun receiveFeedback(feedback: InvalidMoveException) {
        this.feedback = feedback
    }
}

class GeneralPlayer(override val actor: Actor): Player {
    override var feedback: InvalidMoveException? = null
    private var previousAction: Action? = null
    override fun chooseNextMove(): Action {
        return if (previousAction == null) {
            previousAction = Action.MOVE_NORTH
            previousAction!!
        } else {
            if (feedback != null) {
                feedback = null
                previousAction = Action.valueOf(previousAction!!.nextDirectionToTry)
                previousAction!!
            } else {
                previousAction!!
            }
        }
    }
}

class Bradley {

//    val api: WorldApiForAgents
//    val actor: Actor
    
//    val memory: Memory = Memory()
//    val actionControl: ActionControl = ActionControl()
//    val learningUnit: LearningUnit = LearningUnit(memory, actionControl)
//    val visualProcessingUnit: VisualProcessingUnit = VisualProcessingUnit()

/*
    fun chooseNextMove(): Action {
        visualProcessingUnit.see(getVicinity(), learningUnit)
        currentNode = learningUnit.takeAction()
        
    }
*/

/*    private fun getVicinity(): VisualStimuli {
        // TODO how to enforce Bradley not being able to observe arbitrary position?
        return worldApi.getVicinity(actor)
    }

    fun currentPosition(): Coordinate {
        return gameEngine.positionFor(actor)
    }*/
}
