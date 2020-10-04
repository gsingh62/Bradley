package map

import java.lang.UnsupportedOperationException

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
        throw NoSuchElementException("The provided actor is not in the map.")
    }

    override fun moveObject(actor: Actor, deltax: Int, deltay: Int) {
        val coordinate = positionFor(actor)
        val newCoordinate = Coordinate(coordinate.x + deltax, coordinate.y + deltay)
        val oldPlace = nodes[coordinate]
        val newPlace = nodes[newCoordinate]
        if (newPlace is WallNode) {
            throw UnsupportedOperationException("Hit wall node.")

        } else if (oldPlace is OpenSpaceNode && newPlace is OpenSpaceNode) {
            oldPlace.removeObject(actor)
            newPlace.addObject(actor)
        }
    }

    override fun getNode(positionFor: Coordinate): Node {
        return nodes[positionFor] ?: throw NoSuchElementException("The provided coordinate is not valid.")
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
