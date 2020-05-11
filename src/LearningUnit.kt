class LearningUnit {

    fun receiveVisualStimuli(visualStimuli: VisualStimuli) {
        visualStimuli.listOfNodes.forEach {
            nodeContent -> understandVisualStimuli(nodeContent)
        }
    }

    private fun understandVisualStimuli(nodeContent: NodeContent, memory: Memory) {
        val rememberedDangerousAnimalList = memory
        nodeContent.animals.forEach {

        }
    }

    fun takeAction() {

    }
}