package exception

open class InvalidMoveException(message: String) : Exception(message)

const val HIT_WALL_EXCEPTION_MESSAGE: String = "The provided coordinate is not valid."
const val NO_SUCH_COORDINATE_EXCEPTION_MESSAGE: String = "The provided coordinate is not valid."
const val ACTOR_NOT_ON_MAP_EXCEPTION_MESSAGE = "The provided actor is not in the map."

class HitWallException(override val message: String = HIT_WALL_EXCEPTION_MESSAGE) : InvalidMoveException(message)

class PositionNotFoundException(override val message: String = NO_SUCH_COORDINATE_EXCEPTION_MESSAGE) : InvalidMoveException(message)

class ActorNotOnMapException(override val message: String = ACTOR_NOT_ON_MAP_EXCEPTION_MESSAGE) : InvalidMoveException(message)