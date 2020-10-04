package map

interface MapObject {}

class Actor(private val lifeUnits: Int,
            val position: Coordinate) : MapObject {

    val alive: Boolean
        get() = this.lifeUnits > 0

}


interface Node {
}

class ExitNode : Node {}

class OpenSpaceNode(val objects: MutableList<MapObject>) : Node {
}

class WallNode : Node {}
