package output

import com.galexey.bradley.BradleyInterface.Coordinate
import com.galexey.bradley.BradleyInterface.GameData
import com.galexey.bradley.BradleyInterface.Node
import com.galexey.bradley.BradleyInterface.NodeType
import com.galexey.bradley.BradleyInterface.TurnData
import map.ExitNode
import map.OpenSpaceNode
import map.WallNode
import map.WorldMap
import java.io.File
import java.io.FileOutputStream

class SendGameData {
    fun sendGameData(worldMap: WorldMap, radius: Int, turns: List<map.Coordinate>) {
        val gameData = GameData.newBuilder()
        gameData.visibilityRadius = radius
        worldMap.getAllCoordinates().forEach {
            val coordinate = Coordinate.newBuilder()
                    .setX(it.x)
                    .setY(it.y)

            val nodeType = when (worldMap.getNode(it)) {
                is WallNode -> NodeType.WALL_NODE
                is ExitNode -> NodeType.EXIT_NODE
                is OpenSpaceNode -> NodeType.OPEN_NODE
                else -> throw IllegalStateException("Node type can only be wall node, open space node or exit node")
            }
            val node = Node.newBuilder()
                    .setNodeType(nodeType)
                    .setCoordinate(coordinate)

            gameData.addMap(node)
        }
        turns.forEach {
            gameData.addTurns(TurnData.newBuilder()
                    .setHero(Coordinate.newBuilder()
                            .setX(it.x)
                            .setY(it.y)
                    )
            )
        }
        val output = FileOutputStream(File("./bradleygame.pb"))
        gameData.build().writeTo(output)
        output.close()
    }
}