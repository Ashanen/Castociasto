package pl.rockit.castociasto.infrastructure.networking

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.ChallengeHandler
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLSessionAuthChallengePerformDefaultHandling
import platform.Foundation.NSURLSessionAuthChallengeUseCredential
import platform.Foundation.credentialForTrust
import platform.Foundation.serverTrust

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
private val trustAllHandler: ChallengeHandler = { _, _, challenge, completionHandler ->
    val trust = challenge.protectionSpace.serverTrust
    if (trust != null) {
        completionHandler(
            NSURLSessionAuthChallengeUseCredential,
            NSURLCredential.credentialForTrust(trust)
        )
    } else {
        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
    }
}

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual fun createPlatformHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Darwin) {
        engine {
            handleChallenge(trustAllHandler)
        }
        config()
    }
}
