package pl.rockit.castociasto.feature.items.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository
import pl.rockit.castociasto.feature.items.data.dto.PostDto
import pl.rockit.castociasto.feature.items.data.dto.toItem
import pl.rockit.castociasto.foundation.exception.CastociastoException
import pl.rockit.castociasto.infrastructure.networking.safeApiCall

internal class ApiItemRepository(
    private val httpClient: HttpClient,
) : ItemRepository {

    override suspend fun getItems(): List<Item> = safeApiCall {
        httpClient.get("posts")
            .body<List<PostDto>>()
            .map { it.toItem() }
    }

    override suspend fun getItem(id: String): Item? = try {
        safeApiCall {
            httpClient.get("posts/$id")
                .body<PostDto>()
                .toItem()
        }
    } catch (e: CastociastoException.NetworkException.NotFound) {
        null
    }
}
