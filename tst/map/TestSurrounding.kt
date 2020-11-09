package map

import org.junit.Test

class TestSurrounding {

    @Test
    fun surroundingTest() {
        val spiral = Surrounding.getSpiralCoordinates()
        for (coordinate in spiral)
            println(coordinate)
    }
}