package map

import agent.Animal
import agent.Teacher
import java.util.HashSet

class Node(val xCoordinate: Int, val yCoordinate: Int) {
    val animals = HashSet<Animal>()
    val teachers = HashSet<Teacher>()
    val isBlockingNode: Boolean = false
    val isEnd: Boolean = false
}