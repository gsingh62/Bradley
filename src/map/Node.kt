package map

interface MapObject {}

class Actor(private var lifeUnits: Int) : MapObject {

    val alive: Boolean
        get() = this.lifeUnits > 0

    fun loseOneLifeUnit() { lifeUnits-- }
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
