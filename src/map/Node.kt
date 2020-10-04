package map

interface MapObject {}

class Actor(private val lifeUnits: Int,
            var position: Coordinate) : MapObject {

    val alive: Boolean
        get() = this.lifeUnits > 0

}


interface Node {

}

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
