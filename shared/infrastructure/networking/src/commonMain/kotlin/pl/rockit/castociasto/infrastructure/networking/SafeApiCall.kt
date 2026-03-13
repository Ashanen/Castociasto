package pl.rockit.castociasto.infrastructure.networking

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException
import pl.rockit.castociasto.foundation.exception.CastociastoException
import pl.rockit.castociasto.foundation.exception.CastociastoException.NetworkException

suspend fun <T> safeApiCall(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: CastociastoException) {
        throw e
    } catch (e: ClientRequestException) {
        throw when (e.response.status.value) {
            401 -> NetworkException.Unauthorized
            403 -> NetworkException.Forbidden
            404 -> NetworkException.NotFound
            else -> NetworkException.Unknown(e.message)
        }
    } catch (e: ServerResponseException) {
        throw NetworkException.ServerError(e.message)
    } catch (e: Exception) {
        throw NetworkException.NoConnection(e.message ?: "Connection failed")
    }
}
