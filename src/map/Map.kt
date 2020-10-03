package map

import java.util.ArrayList
class Map(private val nodes: Array<Array<Node>>) {
    companion object {
        fun getMap(): Map {
            val node00 = Node(0, 0)
            val node01 = Node(0, 1)
            val node02 = Node(0, 2)
            val node03 = Node(0, 3)
            val node10 = Node(1, 0)
            val node11 = Node(1, 1)
            val node12 = Node(1, 2)
            val node13 = Node(1, 3)
            val node20 = Node(2, 0)
            val node21 = Node(2, 1)
            val node22 = Node(2, 2)
            val node23 = Node(2, 3)
            val graphStructure = arrayOf(
                    arrayOf(node00, node01, node02, node03),
                    arrayOf(node10, node11, node12, node13),
                    arrayOf(node20, node21, node22, node23)
            )
            return Map(graphStructure)
        }

        fun getFirstNode(): Node {
            return getMap().nodes[0][0]
        }
    }


    fun getVicinity(node: Node): VisualStimuli {
        val nodesLookingUp = ArrayList<Node>()
        val nodesLookingDown = ArrayList<Node>()
        val nodesLookingLeft = ArrayList<Node>()
        val nodesLookingRight = ArrayList<Node>()

        (node.xCoordinate - 1 downTo 0).mapTo(nodesLookingUp) { nodes[it][node.yCoordinate] }
        (node.xCoordinate + 1 until 3).mapTo(nodesLookingDown) { nodes[it][node.yCoordinate] }
        (node.yCoordinate - 1 downTo 0).mapTo(nodesLookingRight) { nodes[node.xCoordinate][it] }
        (node.yCoordinate + 1 until 4).mapTo(nodesLookingLeft) { nodes[node.xCoordinate][it] }

        return VisualStimuli(nodesLookingUp, nodesLookingDown, nodesLookingLeft, nodesLookingRight)
    }
}