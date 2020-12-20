package map.generator

import map.Coordinate
import map.CoordinateNodeWorldMap
import map.ExitNode
import map.Node
import map.OpenSpaceNode
import map.WallNode
import map.WorldMap

class MapGenerator(val height: Int, val width: Int, val poolDetails: Set<PoolDetail>, val exitCoordinate: Coordinate) {
    fun generateMap(): WorldMap {
        val nodes = mutableMapOf<Coordinate, Node>()
        nodes[exitCoordinate] = ExitNode()
        for(x in 0..width) {
            for(y in 0..height) {
                val coordinate = Coordinate(x, y)
                val poolDetail = coordinateIsAtStartOfPool(coordinate, poolDetails)
                if (poolDetail != null) {
                    for (i in 0..poolDetail.width) {
                        for (j in 0..poolDetail.height) {
                            nodes[Coordinate(coordinate.x + i, coordinate.y + j)] = WallNode()
                        }
                    }
                } else {
                    if (nodes[coordinate] == null) {
                        nodes[coordinate] = OpenSpaceNode()
                    }
                }
            }
        }
        return CoordinateNodeWorldMap(nodes)
    }

    private fun coordinateIsAtStartOfPool(coordinate: Coordinate, poolDetails: Set<PoolDetail>): PoolDetail? {
        return poolDetails.firstOrNull { poolDetail -> poolDetail.topLeftCoordinate == coordinate }
    }
}

data class PoolDetail(val topLeftCoordinate: Coordinate, val height: Int, val width: Int) {}