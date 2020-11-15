package map

import action.Move
import exception.ActorNotOnMapException
import exception.HitWallException
import exception.InvalidMoveException
import exception.InvalidVectorException
import exception.PositionNotFoundException

data class Coordinate(val x: Int, val y: Int) {
    fun add (vector: Vector): Coordinate {
        return Coordinate(this.x + vector.deltax,this.y + vector.deltay)
    }
}

data class Vector (val deltax: Int, val deltay: Int)

interface WorldMap {
    fun positionFor(mapObject: MapObject): Coordinate
    fun getNode(positionFor: Coordinate): Node
    fun moveObject(mapObject: MapObject, vector: Vector)
    fun getAllCoordinates(): Set<Coordinate>
    fun getSurrounding(actor: Actor, radius: Int): WorldMap
    fun getWorldRules(): WorldRules
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

    override fun getSurrounding(actor: Actor, radius: Int): WorldMap {
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

    override fun getWorldRules():  WorldRules {
        val preventTeleportation: (Move, Coordinate) -> InvalidMoveException? = { m: Move, c: Coordinate ->
            if (!(Math.abs(m.vector.deltax) <= 1 && Math.abs(m.vector.deltay) <= 1))
                InvalidVectorException() else null
        }

        val preventCrossingBoundaries: (Move, Coordinate) -> InvalidMoveException? = { m: Move, c: Coordinate ->
            val proposedPosition = Coordinate(m.vector.deltax + c.x, m.vector.deltay + c.y)
            try {
                getNode(proposedPosition)
                null
            } catch (ex: PositionNotFoundException) {
                InvalidVectorException()
            }
        }
        return WorldRules(listOf(preventCrossingBoundaries, preventTeleportation))
    }
}

class WorldRules(val worldRulesList: List<(Move, Coordinate) -> InvalidMoveException?>)

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
                    startingActor = Actor(60)
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
