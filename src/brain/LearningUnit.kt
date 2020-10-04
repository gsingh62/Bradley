package brain

import map.Coordinate
import map.VisualStimuli
import map.Node
import map.WallNode

class LearningUnit(val memory: Memory) {

    fun receiveVisualStimuli(visualStimuli: VisualStimuli) {
        val currentPosition = Coordinate(0,0 )
        for (y in currentPosition.y until currentPosition.y+3) {
            if (visualStimuli.vicinity[Coordinate(currentPosition.x, y)] is WallNode) {
                break
            }
        }
    }

    private fun understandVisualStimuli(nodeContent: Node) {
/*        if (nodeContent.isEnd) {

        } else if (nodeContent.isBlockingNode) {

        } else if (!nodeContent.animals.isEmpty()) {

        } else if (!nodeContent.teachers.isEmpty()) {

        }*/
    }

//    fun takeAction(): Node {
//
//    }
}