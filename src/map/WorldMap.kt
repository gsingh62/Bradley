package map

import exception.ActorNotOnMapException
import exception.HitWallException
import exception.PositionNotFoundException
import java.lang.IllegalStateException

data class Coordinate(val x: Int, val y: Int) {
    fun add (vector: Vector): Coordinate {
        return Coordinate(this.x + vector.deltax,this.y + vector.deltay)
    }
}

data class Vector (val deltax: Int, val deltay: Int)

interface WorldMap {
    fun positionFor(mapObject: MapObject): Coordinate
    fun getNode(positionFor: Coordinate): Node
    fun moveObject(actor: MapObject, vector: Vector)
    fun getAllCoordinates(): Set<Coordinate>
    fun getSurrounding(actor: Actor): WorldMap
}

class Memory(private val nodes: MutableMap<Coordinate, Node>) {

    companion object {
        fun init(coordinates: Set<Coordinate>): Memory {
            val nodes: MutableMap<Coordinate, Node> = mutableMapOf()
            for(coordinate in coordinates) {
                nodes[coordinate] = OpenSpaceNode()
            }
            return Memory(nodes)
        }
    }
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

open class CoordinateNodeWorldMap(private val nodes: MutableMap<Coordinate, Node>) : WorldMap {
    override fun getAllCoordinates(): Set<Coordinate> =
        nodes.keys

    override fun positionFor(mapObject: MapObject): Coordinate {
        nodes.forEach { (k, v) ->
            if (v is OpenSpaceNode) {
                if (v.objects.size > 0 ) {
                    if (mapObject == v.objects[0]) {
                        return k
                    }
                }
            }
         }
        throw ActorNotOnMapException()
    }

    override fun moveObject(mapObject: MapObject, vector: Vector) {
        val coordinate = positionFor(mapObject)
        val newCoordinate = coordinate.add(vector)
        val oldPlace = getNode(coordinate)
        val newPlace = getNode(newCoordinate)

        if (newPlace is WallNode) {
            throw HitWallException()
        } else if (oldPlace is OpenSpaceNode && newPlace is OpenSpaceNode) {
            oldPlace.removeObject(mapObject)
            newPlace.addObject(mapObject)
        }
    }

    override fun getSurrounding(actor: Actor): WorldMap {
        val radius = 2
        val surroundingNodes: MutableMap<Coordinate, Node> = mutableMapOf()
        val pos = positionFor(actor)
        for (coordinate in getAllCoordinates()) {
            if (Math.sqrt(Math.pow((pos.x - coordinate.x).toDouble(), 2.0) +
                    Math.pow((pos.y - coordinate.y).toDouble(), 2.0)) <= radius) {
                surroundingNodes[coordinate] = getNode(coordinate)
            }
        }
        return CoordinateNodeWorldMap(surroundingNodes)
    }

    override fun getNode(positionFor: Coordinate): Node {
        return nodes[positionFor] ?: throw PositionNotFoundException()
    }
}

class WorldMapBuilder {
    private val nodes = mutableMapOf<Coordinate, Node>()
    private lateinit var startingActor: Actor
    private lateinit var exitPosition: Coordinate
    fun load(s: String): WorldMapBuilder {
        var x = 0
        var y = -1
        for (i in s.indices) {
            val coordinate = Coordinate(x, y)
            val node = when(s[i]) {
                's' ->  {
                    startingActor = Actor(20)
                    OpenSpaceNode().apply { addObject(startingActor) }
                }
                'e' -> {
                    exitPosition = coordinate
                    ExitNode()
                }
                'X' -> WallNode()
                ' ' -> continue
                '"' -> continue
                else -> OpenSpaceNode()
            }

            if (s[i] == '\n') {
                y++
                x = 0
            } else {
                nodes[coordinate] = node
                x++
            }
        }
        return this
    }

    fun build(): WorldMap {
        return CoordinateNodeWorldMap(nodes)
    }

    fun getStartingActor(): Actor {
        return startingActor
    }

    fun getExitPosition(): Coordinate {
        return exitPosition
    }
}
