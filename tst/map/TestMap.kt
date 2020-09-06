package map

import org.junit.Assert
import org.junit.Test

class TestMap {

    @Test
    fun givesOutTheVicinity() {
        val map = Map.getMap()

        val visualStimuli = map.getVicinity(Node(1,1))
        Assert.assertEquals(1, visualStimuli.nodesLookingUp.size)
        Assert.assertEquals(0, visualStimuli.nodesLookingUp[0].xCoordinate)
        Assert.assertEquals(1, visualStimuli.nodesLookingUp[0].yCoordinate)

        Assert.assertEquals(1, visualStimuli.nodesLookingRight.size)
        Assert.assertEquals(1, visualStimuli.nodesLookingRight[0].xCoordinate)
        Assert.assertEquals(0, visualStimuli.nodesLookingRight[0].yCoordinate)

        Assert.assertEquals(1, visualStimuli.nodesLookingDown.size)
        Assert.assertEquals(2, visualStimuli.nodesLookingDown[0].xCoordinate)
        Assert.assertEquals(1, visualStimuli.nodesLookingDown[0].yCoordinate)

        Assert.assertEquals(2, visualStimuli.nodesLookingLeft.size)
        Assert.assertEquals(1, visualStimuli.nodesLookingLeft[0].xCoordinate)
        Assert.assertEquals(2, visualStimuli.nodesLookingLeft[0].yCoordinate)
        Assert.assertEquals(1, visualStimuli.nodesLookingLeft[1].xCoordinate)
        Assert.assertEquals(3, visualStimuli.nodesLookingLeft[1].yCoordinate)
    }
}
