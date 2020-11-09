package map

import exception.ActorNotOnMapException
import exception.HitWallException
import exception.PositionNotFoundException
import game.Game
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException

data class Coordinate(val x: Double, val y: Double) {
    fun add (vector: Vector): Coordinate {
        return Coordinate(this.x + vector.deltax,this.y + vector.deltay)
    }
}

data class Vector (val deltax: Double, val deltay: Double)

interface WorldMap {
    fun positionFor(mapObject: MapObject): Coordinate
    fun getNode(positionFor: Coordinate): Node
    fun moveObject(actor: MapObject, vector: Vector)
    fun getAllCoordinates(): Set<Coordinate>
    fun getSurrounding(actor: Actor): Surrounding
}

class Memory(private val nodes: MutableMap<Coordinate, Node>) {

    val visitedNodes = HashSet<Coordinate>()
    companion object {
        fun init(coordinates: Set<Coordinate>): Memory {
            val nodes: MutableMap<Coordinate, Node> = mutableMapOf()
            for(coordinate in coordinates) {
                nodes[coordinate] = OpenSpaceNode()
            }
            return Memory(nodes)
        }
    }
    fun furnishMemoryWithSurrounding(surrounding: Surrounding) {
        for (coordinate in surrounding.getAllCoordinates()) {
            nodes[coordinate] = surrounding.getNode(coordinate)
        }
    }

    fun getNode(coordinate: Coordinate): Node {
        return nodes[coordinate] ?: throw IllegalStateException("Node not found in this position")
    }

    fun markNodeVisited(node: Coordinate) {
        visitedNodes.add(node)
    }

    fun isNodeVisited(node: Coordinate): Boolean {
        return visitedNodes.contains(node)
    }

    fun getAllCoordinates(): Set<Coordinate> = nodes.keys
}

open class CoordinateNodeWorldMap(private val nodes: MutableMap<Coordinate, Node>) : WorldMap {
    private val log = LoggerFactory.getLogger(CoordinateNodeWorldMap::class.java)

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
        log.info("asked to move actor to {} {}", vector.deltax, vector.deltay )
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

    override fun getSurrounding(actor: Actor): Surrounding {
        val radius = 2
        val surroundingNodes: MutableMap<Coordinate, Node> = mutableMapOf()
        val pos = positionFor(actor)
        for (coordinate in getAllCoordinates()) {
            if (Math.sqrt(Math.pow((pos.x - coordinate.x), 2.0) +
                    Math.pow((pos.y - coordinate.y), 2.0)) <= radius) {
                surroundingNodes[coordinate] = getNode(coordinate)
            }
        }
        return Surrounding(surroundingNodes)
    }

    override fun getNode(positionFor: Coordinate): Node {
        return nodes[positionFor] ?: throw PositionNotFoundException()
    }
}

class WorldMapBuilder {
    private val nodes = mutableMapOf<Coordinate, Node>()
    private lateinit var startingActor: Actor
    private lateinit var exitPosition: Coordinate

    /**
     * stringMap: The string representation of the map's main coordinates
     * additionalEmptyCoordinates: In some map navigation algorithms additional non-integer and
     * negative coordinates will be required. For example, for the exploratory player with no end node provided.
     */
    fun load(stringMap: String, additionalEmptyCoordinates: List<Coordinate>? = null): WorldMapBuilder {
        var x = 0
        var y = -1
        for (i in stringMap.indices) {
            val coordinate = Coordinate(x.toDouble(), y.toDouble())
            val node = when(stringMap[i]) {
                's' ->  {
                    startingActor = Actor(50)
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

            if (stringMap[i] == '\n') {
                y++
                x = 0
            } else {
                nodes[coordinate] = node
                x++
            }
        }
        if (additionalEmptyCoordinates != null) {
            for (coordinate in additionalEmptyCoordinates) {
                if (!nodes.containsKey(coordinate)) {
                    nodes[coordinate] = OpenSpaceNode()
                }
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
