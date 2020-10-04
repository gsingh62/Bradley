package exception

class ActorNotOnMapException(override val message: String = ACTOR_NOT_ON_MAP_EXCEPTION_MESSAGE) : InvalidMoveException(message) {
    companion object {
        const val ACTOR_NOT_ON_MAP_EXCEPTION_MESSAGE = "The provided actor is not in the map."
    }
}