package map

interface MapObject {}

class Actor(private var lifeUnits: Int) : MapObject {
    private var surrounding: WorldMap? = null
    val alive: Boolean
        get() = this.lifeUnits > 0

    fun loseOneLifeUnit() { lifeUnits-- }
    fun getSurrounding(): WorldMap {
        return surrounding!!
    }
    fun setSurrounding(surrounding: WorldMap) {
        this.surrounding = surrounding
    }
}

interface Node {
}

class ExitNode : OpenSpaceNode() {}

open class OpenSpaceNode : Node {
    val objects: MutableList<MapObject> = mutableListOf()

    fun removeObject(actor: MapObject) {
        objects.remove(actor)
    }

    fun addObject(actor: MapObject) {
        objects.add(actor)
    }
}

class WallNode : Node {
}
