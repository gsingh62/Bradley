package exception

class ExceptionMessages {
    companion object {
        const val WALL_FEEDBACK = "You cannot go forward, as you have hit a wall."
        const val HIT_WALL_EXCEPTION_MESSAGE = "Hit wall node."
        const val NO_SUCH_COORDINATE_EXCEPTION_MESSAGE = "The provided coordinate is not valid."
        const val ACTOR_NOT_ON_MAP_EXCEPTION_MESSAGE = "The provided actor is not in the map."
    }
}