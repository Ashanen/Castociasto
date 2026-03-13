package pl.rockit.castociasto.foundation.exception

sealed class CastociastoException(message: String) : Exception(message) {

    sealed class NetworkException(message: String) : CastociastoException(message) {
        data object Unauthorized : NetworkException("Unauthorized")
        data object Forbidden : NetworkException("Forbidden")
        data object NotFound : NetworkException("Resource not found")
        data class ServerError(val detail: String) : NetworkException("Server error: $detail")
        data class NoConnection(val detail: String) : NetworkException("No connection: $detail")
        data class Unknown(val detail: String) : NetworkException("Unknown network error: $detail")
    }

    sealed class DataException(message: String) : CastociastoException(message) {
        data class NotFound(val detail: String) : DataException(detail)
    }
}
