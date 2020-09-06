package game

import agent.Bradley
import map.Map

class Game(private val bradley: Bradley, private val map: Map) {

    var timeCounter: Int = 0

    fun run() {
        val game = Game(bradley, Map.getMap())
        while(!game.checkIfGameOver()) {
            game.makeTurn()
        }
    }

    fun makeTurn() {
        incrementTime()
        bradley.chooseNextMove()
    }

    private fun incrementTime() {
        timeCounter++
        bradley.lifeUnits--
    }

    private fun checkIfGameOver(): Boolean {
        return bradley.lifeUnits == 0 || bradley.currentNode.isEnd
    }
}