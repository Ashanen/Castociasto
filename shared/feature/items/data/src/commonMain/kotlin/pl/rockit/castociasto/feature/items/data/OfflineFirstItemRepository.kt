package pl.rockit.castociasto.feature.items.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.rockit.castociasto.core.events.AppEvent
import pl.rockit.castociasto.core.events.AppEventBus
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository
import pl.rockit.castociasto.feature.items.data.dto.PostDto
import pl.rockit.castociasto.infrastructure.database.ItemDao
import pl.rockit.castociasto.infrastructure.database.ItemEntity
import pl.rockit.castociasto.infrastructure.networking.safeApiCall

internal class OfflineFirstItemRepository(
    private val httpClient: HttpClient,
    private val itemDao: ItemDao,
    private val eventBus: AppEventBus,
) : ItemRepository {

    override suspend fun getItems(): List<Item> =
        itemDao.getAll().map { it.toItem() }

    override suspend fun getItem(id: String): Item? =
        itemDao.getById(id)?.toItem()

    override fun observeItems(): Flow<List<Item>> =
        itemDao.observeAll().map { entities -> entities.map { it.toItem() } }

    override fun observeItem(id: String): Flow<Item?> =
        itemDao.observeById(id).map { it?.toItem() }

    override suspend fun refresh() {
        val posts = safeApiCall {
            httpClient.get("posts").body<List<PostDto>>()
        }
        itemDao.upsertAll(posts.map { it.toEntity() })
        eventBus.emit(AppEvent.ItemsUpdated)
    }
}

private fun PostDto.toEntity() = ItemEntity(
    id = id.toString(),
    title = title.replaceFirstChar { it.uppercase() },
    subtitle = body.replace("\n", " "),
)

private fun ItemEntity.toItem() = Item(
    id = id,
    title = title,
    subtitle = subtitle,
)
