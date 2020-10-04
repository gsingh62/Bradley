package agent

import action.Action
import brain.LearningUnit
import brain.Memory
import brain.VisualProcessingUnit
import map.*

interface Player {
    fun chooseNextMove(): Action
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
