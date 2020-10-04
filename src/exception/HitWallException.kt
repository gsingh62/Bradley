package exception

class HitWallException(override val message: String = HIT_WALL_EXCEPTION_MESSAGE) : InvalidMoveException(message) {
    companion object {
        const val HIT_WALL_EXCEPTION_MESSAGE: String = "The provided coordinate is not valid."
    }
}