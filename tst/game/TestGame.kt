package game

import agent.Bradley
import map.Map
import org.junit.Assert
import org.junit.Test

class TestGame {

    @Test
    fun increasesTimeWithEachTurn() {
        val bradley = Bradley()
        val game = Game(bradley, Map.getMap())

        Assert.assertEquals(0, game.timeCounter)
        game.makeTurn()
        Assert.assertEquals(1, game.timeCounter)
    }

    @Test
    fun tracksPositionWhenBradleyMoves() {
        val bradley = Bradley()
        val game = Game(bradley, Map.getMap())

        Assert.assertEquals(0, bradley.currentNode.xCoordinate)
        Assert.assertEquals(0, bradley.currentNode.yCoordinate)
        game.makeTurn()
        Assert.assertNotEquals(0, bradley.currentNode.xCoordinate)
        Assert.assertNotEquals(0, bradley.currentNode.yCoordinate)
    }
}