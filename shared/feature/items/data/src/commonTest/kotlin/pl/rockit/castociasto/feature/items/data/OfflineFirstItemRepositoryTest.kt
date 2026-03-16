package pl.rockit.castociasto.feature.items.data

import app.cash.turbine.test
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import pl.rockit.castociasto.core.events.AppEvent
import pl.rockit.castociasto.core.events.AppEventBus
import pl.rockit.castociasto.foundation.exception.CastociastoException
import pl.rockit.castociasto.infrastructure.database.ItemDao
import pl.rockit.castociasto.infrastructure.database.ItemEntity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class OfflineFirstItemRepositoryTest {

    private val itemDao = FakeItemDao()
    private val eventBus = FakeAppEventBus()

    private fun createClient(): HttpClient {
        val engine = MockEngine { request ->
            when {
                request.url.encodedPath == "/posts" -> respond(
                    content = POSTS_JSON,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
                else -> respond(content = "", status = HttpStatusCode.NotFound)
            }
        }
        return HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }

    private fun createErrorClient(): HttpClient {
        val engine = MockEngine { respond(content = "", status = HttpStatusCode.InternalServerError) }
        return HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }

    @Test
    fun `getItems returns items from DAO`() = runTest {
        val entities = listOf(
            ItemEntity(id = "1", title = "Apple", subtitle = "Fruit"),
            ItemEntity(id = "2", title = "Banana", subtitle = "Fruit"),
        )
        itemDao.items.value = entities
        val repository = OfflineFirstItemRepository(createClient(), itemDao, eventBus)

        val items = repository.getItems()

        assertEquals(2, items.size)
        assertEquals("Apple", items[0].title)
    }

    @Test
    fun `getItem returns item from DAO`() = runTest {
        itemDao.items.value = listOf(
            ItemEntity(id = "1", title = "Apple", subtitle = "Fruit"),
        )
        val repository = OfflineFirstItemRepository(createClient(), itemDao, eventBus)

        val item = repository.getItem("1")

        assertEquals("1", item?.id)
        assertEquals("Apple", item?.title)
    }

    @Test
    fun `getItem returns null for non-existent id`() = runTest {
        val repository = OfflineFirstItemRepository(createClient(), itemDao, eventBus)

        val item = repository.getItem("999")

        assertNull(item)
    }

    @Test
    fun `observeItems emits from DAO`() = runTest {
        val repository = OfflineFirstItemRepository(createClient(), itemDao, eventBus)

        repository.observeItems().test {
            assertEquals(emptyList(), awaitItem())

            itemDao.items.value = listOf(
                ItemEntity(id = "1", title = "Apple", subtitle = "Fruit"),
            )
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals("Apple", items[0].title)
        }
    }

    @Test
    fun `observeItem emits single item from DAO`() = runTest {
        val repository = OfflineFirstItemRepository(createClient(), itemDao, eventBus)

        repository.observeItem("1").test {
            assertNull(awaitItem())

            itemDao.items.value = listOf(
                ItemEntity(id = "1", title = "Apple", subtitle = "Fruit"),
                ItemEntity(id = "2", title = "Banana", subtitle = "Fruit"),
            )
            val item = awaitItem()
            assertEquals("1", item?.id)
            assertEquals("Apple", item?.title)
        }
    }

    @Test
    fun `refresh fetches from API and stores in DAO`() = runTest {
        val repository = OfflineFirstItemRepository(createClient(), itemDao, eventBus)

        repository.refresh()

        assertEquals(2, itemDao.items.value.size)
        assertEquals("Test title", itemDao.items.value[0].title)
    }

    @Test
    fun `refresh emits ItemsUpdated event`() = runTest {
        val repository = OfflineFirstItemRepository(createClient(), itemDao, eventBus)

        eventBus.events.test {
            repository.refresh()
            val event = awaitItem()
            assertIs<AppEvent.ItemsUpdated>(event)
        }
    }

    @Test
    fun `refresh throws NetworkException on server error`() = runTest {
        val repository = OfflineFirstItemRepository(createErrorClient(), itemDao, eventBus)

        assertFailsWith<CastociastoException.NetworkException> {
            repository.refresh()
        }
    }

    companion object {
        private val POSTS_JSON = """
            [
                {"userId": 1, "id": 1, "title": "test title", "body": "Test body content"},
                {"userId": 1, "id": 2, "title": "another post", "body": "Another\nbody"}
            ]
        """.trimIndent()
    }
}

private class FakeItemDao : ItemDao {

    val items = MutableStateFlow<List<ItemEntity>>(emptyList())

    override fun observeAll(): Flow<List<ItemEntity>> = items

    override fun observeById(id: String): Flow<ItemEntity?> =
        items.map { list -> list.find { it.id == id } }

    override suspend fun getAll(): List<ItemEntity> = items.value

    override suspend fun getById(id: String): ItemEntity? =
        items.value.find { it.id == id }

    override suspend fun upsertAll(items: List<ItemEntity>) {
        this.items.value = items
    }

    override suspend fun deleteAll() {
        items.value = emptyList()
    }
}

private class FakeAppEventBus : AppEventBus {
    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 64)
    override val events: SharedFlow<AppEvent> = _events.asSharedFlow()
    override suspend fun emit(event: AppEvent) { _events.emit(event) }
}
