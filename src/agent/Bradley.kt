package agent

import action.Action
import action.Move
import exception.InvalidMoveException
import map.Actor
import map.Coordinate
import map.Node
import map.OpenSpaceNode
import map.Vector
import map.WorldMap

interface Player {
    val actor: Actor
    var feedback: InvalidMoveException?
    fun chooseNextMove(): Action
    fun receiveFeedback(feedback: InvalidMoveException) {
        this.feedback = feedback
    }
}

class GeneralPlayer(override val actor: Actor): Player {
    private val options = listOf(Vector(1,-1), Vector(1,0), Vector(1, 1),
    Vector(0,1),Vector(-1, 1), Vector(-1, 0), Vector(-1, -1),
    Vector(0, -1))
    override var feedback: InvalidMoveException? = null
    private var previousActionIndex = 0
    override fun chooseNextMove(): Action {
        if (feedback != null) {
            feedback = null
            val newActionIndex = (previousActionIndex + 1) % 8
            previousActionIndex = newActionIndex
        }
        return Move(options[previousActionIndex])
    }
}

private fun fillWithOpenNodes(coordinates: Set<Coordinate>): MutableMap<Coordinate, Node> {
    val nodes = HashMap<Coordinate, Node>()
    for(coordinate in coordinates) {
        nodes[coordinate] = OpenSpaceNode()
    }
    return nodes
}

class Memory(private val nodes: MutableMap<Coordinate, Node>) {

    constructor(coordinates: Set<Coordinate>) : this(fillWithOpenNodes(coordinates))

    fun furnishMemoryWithSurrounding(surrounding: WorldMap) {
        for (coordinate in surrounding.getAllCoordinates()) {
            nodes[coordinate] = surrounding.getNode(coordinate)
        }
    }

    fun getNode(coordinate: Coordinate): Node {
        return nodes[coordinate] ?: throw IllegalStateException("Node not found in this position")
    }

    fun getAllCoordinates(): Set<Coordinate> = nodes.keys
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
