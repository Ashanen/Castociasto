package pl.rockit.castociasto.infrastructure.networking.di

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import pl.rockit.castociasto.infrastructure.networking.createPlatformHttpClient

val networkingModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            prettyPrint = false
        }
    }

    single {
        createPlatformHttpClient {
            defaultRequest {
                url("https://jsonplaceholder.typicode.com/")
            }
            install(ContentNegotiation) {
                json(get())
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            expectSuccess = true
        }
    }
}
