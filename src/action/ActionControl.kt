package action

import map.Vector

interface Action {
}

class Move(val vector: Vector): Action {}
