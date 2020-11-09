package map

import exception.ActorNotOnMapException
import exception.PositionNotFoundException
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class Surrounding(private val nodes: MutableMap<Coordinate, Node>) {

    companion object {
        fun getSpiralCoordinates(): List<Coordinate> {
            val spiralCoordinates = ArrayList<Coordinate>()
            val r = 0.5 / Math.PI
            val d = 1.0
            for (n in 0..500) {
                val t = sqrt(2.0 * d * n / r)
                val rt = r * t

                val x =  rt * cos(t)
                val y =  rt * sin(t)
                spiralCoordinates.add(Coordinate(x, y))
            }
            return spiralCoordinates
        }
    }
    fun getAllCoordinates(): Set<Coordinate> = nodes.keys

    fun getNode(positionFor: Coordinate): Node {
        return nodes[positionFor] ?: throw PositionNotFoundException()
    }

    fun positionFor(mapObject: MapObject): Coordinate {
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
}