package map

import java.util.*

interface MapObject {}

class Actor(private var lifeUnits: Int) : MapObject {

    private val nodes = ArrayList<Coordinate>()
    val alive: Boolean
        get() = this.lifeUnits > 0

    fun loseOneLifeUnit() { lifeUnits-- }

    fun addNextNodeCoordinate(coordinate: Coordinate) {
        nodes.add(coordinate)
    }

    fun getNextNodeCoordinate(): Coordinate {
        return nodes.removeAt(0)
    }

    fun isNodeQueueEmpty(): Boolean {
        return nodes.isEmpty()
    }
}

interface Node {}

class ExitNode() : OpenSpaceNode() {}

open class OpenSpaceNode(private val discovered: Boolean = false) : Node {
    val objects: MutableList<MapObject> = mutableListOf()
    fun removeObject(actor: MapObject) {
        objects.remove(actor)
    }

    fun addObject(actor: MapObject) {
        objects.add(actor)
    }
}

class WallNode : Node {}
