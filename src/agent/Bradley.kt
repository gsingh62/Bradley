package agent

import action.Action
import map.Actor

interface Player {
    val actor: Actor
    var feedback: String
    fun chooseNextMove(): Action
    fun receiveFeedback(feedback: String) {
        this.feedback = feedback
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
