package exception

class PositionNotFoundException(override val message: String = NO_SUCH_COORDINATE_EXCEPTION_MESSAGE) : InvalidMoveException(message) {
    companion object {
        const val NO_SUCH_COORDINATE_EXCEPTION_MESSAGE: String = "The provided coordinate is not valid."
    }
}