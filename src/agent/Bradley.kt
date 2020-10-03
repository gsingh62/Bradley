package agent

import action.ActionControl
import brain.LearningUnit
import brain.Memory
import brain.VisualProcessingUnit
import map.Node
import map.Map

class Bradley {
    var lifeUnits = 10
    var currentNode =  Map.getFirstNode()
    val memory: Memory = Memory()
    val learningUnit: LearningUnit = LearningUnit(memory)
    val visualProcessingUnit: VisualProcessingUnit = VisualProcessingUnit()
    val actionControl: ActionControl = ActionControl()

    fun chooseNextMove() {
        // dumb implementation for now, keeps travelling down then left.
        val previousNode = currentNode
        currentNode = Node(previousNode.xCoordinate + 1, previousNode.yCoordinate +1)
    }
}
