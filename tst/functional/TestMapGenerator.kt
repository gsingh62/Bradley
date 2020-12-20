package functional

import exception.PositionNotFoundException
import map.Coordinate
import map.ExitNode
import map.WallNode
import map.WorldMap
import map.generator.MapGenerator
import map.generator.PoolDetail
import org.junit.Assert
import org.junit.Test

class TestMapGenerator {
    @Test
    fun testMapGenerator() {
        val exitCoordinate = Coordinate(1,1)
        val height = 5
        val width = 6
        val poolDetails = setOf(PoolDetail(Coordinate(3,3), 2, 2))
        val mapGenerator = MapGenerator(height, width, poolDetails, exitCoordinate)
        val worldMap = mapGenerator.generateMap()

        verifyExitNode(worldMap, exitCoordinate)
        verifyPoolDetails(worldMap, poolDetails)
        verifyHeightAndWidth(worldMap, height, width)
    }

    private fun verifyExitNode(worldMap: WorldMap, exitCoordinate: Coordinate) {
        Assert.assertTrue(worldMap.getNode(exitCoordinate) is ExitNode)
    }

    private fun verifyPoolDetails(worldMap: WorldMap, poolDetails: Set<PoolDetail>) {
        for(poolDetail in poolDetails) {
            poolDetail.height
            poolDetail.width
            poolDetail.topLeftCoordinate

            for (x in 0..poolDetail.width) {
                for (y in 0..poolDetail.height) {
                    Assert.assertTrue(worldMap.getNode(Coordinate(poolDetail.topLeftCoordinate.x + x,
                        poolDetail.topLeftCoordinate.y + y)) is WallNode)
                }
            }
        }
    }

    private fun verifyHeightAndWidth(worldMap: WorldMap, height: Int, width: Int) {
        worldMap.getNode(Coordinate(width, height))
        Assert.assertThrows(PositionNotFoundException::class.java) {
            worldMap.getNode(Coordinate(width + 1, height + 1))
        }
    }
}