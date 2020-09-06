package brain

import map.VisualStimuli

class VisualProcessingUnit {

    fun see(visualStimuli: VisualStimuli, learningUnit: LearningUnit) {
        sendToLearningUnit(visualStimuli, learningUnit)
    }

    private fun sendToLearningUnit(visualStimuli: VisualStimuli, learningUnit: LearningUnit) {
        learningUnit.receiveVisualStimuli(visualStimuli)
    }
}
