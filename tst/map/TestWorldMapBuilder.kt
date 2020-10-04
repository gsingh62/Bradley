package map

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test

class TestWorldMapBuilder {
    @Test
    fun simpleMapIsBuiltCorrectly() {
        val builder = WorldMapBuilder()
                .load("""
                    .e.
                    ...
                    .s.
                    """)

        val actor: Actor = builder.getStartingActor()
        val exitPosition: Coordinate = builder.getExitPosition()
        val map = builder.build()

        assertThat(map.positionFor(actor), equalTo(Coordinate(1, 2)))
        assertThat(exitPosition, equalTo(Coordinate(1, 0)))

        assertThat(map.getNode(exitPosition), instanceOf(ExitNode::class.java))

        val actorNode = map.getNode(map.positionFor(actor))
        assertThat(actorNode, instanceOf(OpenSpaceNode::class.java))
        if (actorNode is OpenSpaceNode) {
            assertThat(actorNode.objects, contains(actor))
        }
    }
}

