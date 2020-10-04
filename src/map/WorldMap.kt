package map

import exception.ExceptionMessages.Companion.ACTOR_NOT_ON_MAP_EXCEPTION_MESSAGE
import exception.ExceptionMessages.Companion.HIT_WALL_EXCEPTION_MESSAGE
import exception.ExceptionMessages.Companion.NO_SUCH_COORDINATE_EXCEPTION_MESSAGE
import exception.InvalidMoveException

data class Coordinate(val x: Int, val y: Int)

interface WorldMap {
    fun positionFor(actor: Actor): Coordinate
    fun getNode(positionFor: Coordinate): Node
    fun moveObject(actor: Actor, deltax: Int, deltay: Int)
}

class CoordinateNodeWorldMap(private val nodes: MutableMap<Coordinate, Node>) : WorldMap {
    override fun positionFor(actor: Actor): Coordinate {
        nodes.forEach { (k, v) ->
            if (v is OpenSpaceNode) {
                if (v.objects.size > 0 && v.objects[0] is Actor) {
                    val actorInOpenSpaceNode = v.objects[0] as Actor
                    if (actor == actorInOpenSpaceNode) {
                        return k
                    }
                }
            }
         }
        throw NoSuchElementException(ACTOR_NOT_ON_MAP_EXCEPTION_MESSAGE)
    }

    override fun moveObject(actor: Actor, deltax: Int, deltay: Int) {
        actor.loseOneLifeUnit()
        val coordinate = positionFor(actor)
        val newCoordinate = Coordinate(coordinate.x + deltax, coordinate.y + deltay)
        val oldPlace = nodes[coordinate]
        val newPlace = nodes[newCoordinate]
        if (newPlace is WallNode) {
            throw InvalidMoveException(HIT_WALL_EXCEPTION_MESSAGE)

        } else if (oldPlace is OpenSpaceNode && newPlace is OpenSpaceNode) {
            oldPlace.removeObject(actor)
            newPlace.addObject(actor)
        }
    }

    override fun getNode(positionFor: Coordinate): Node {
        return nodes[positionFor] ?: throw NoSuchElementException(NO_SUCH_COORDINATE_EXCEPTION_MESSAGE)
    }
}

class WorldMapBuilder() {
    private val nodes = mutableMapOf<Coordinate, Node>()
    private lateinit var startingActor: Actor
    private lateinit var exitPosition: Coordinate
    fun load(s: String): WorldMapBuilder {
        var x = 0
        var y = 0
        for (i in s.indices) {
            val coordinate = Coordinate(x, y-1)
            val node = when(s[i]) {
                's' ->  {
                    startingActor = Actor(10)
                    OpenSpaceNode().apply{addObject(startingActor)}
                }
                'e' -> {
                    exitPosition = coordinate
                    ExitNode()
                }
                'X' -> WallNode()
                ' ' -> continue
                else -> OpenSpaceNode()
            }
            nodes[coordinate] = node
            x++
            if (s[i] == '\n') {
                y++
                x = 0
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
