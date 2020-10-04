package map

import action.Action
import agent.Player

interface MapObject {}

class Actor(private val lifeUnits: Int) : MapObject {

    val alive: Boolean
        get() = this.lifeUnits > 0

}

class AlwaysGoUpPlayer(override val actor: Actor): Player {
    override lateinit var feedback: String

    override fun chooseNextMove(): Action {
        return Action.MOVE_NORTH
    }
}

interface Node {}

class ExitNode() : OpenSpaceNode() {}

open class OpenSpaceNode() : Node {
    val objects: MutableList<MapObject> = mutableListOf()
    fun removeObject(actor: MapObject) {
        objects.remove(actor)
    }

    fun addObject(actor: MapObject) {
        objects.add(actor)
    }
}

class WallNode : Node {}
