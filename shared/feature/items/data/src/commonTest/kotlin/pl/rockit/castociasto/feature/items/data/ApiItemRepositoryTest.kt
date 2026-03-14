package pl.rockit.castociasto.feature.items.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import pl.rockit.castociasto.foundation.exception.CastociastoException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ApiItemRepositoryTest {

    private fun createClient(handler: MockEngine.() -> Unit = {}): HttpClient {
        val engine = MockEngine { request ->
            when {
                request.url.encodedPath == "/posts" -> respond(
                    content = POSTS_JSON,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
                request.url.encodedPath.startsWith("/posts/") -> {
                    val id = request.url.encodedPath.removePrefix("/posts/")
                    val post = POSTS_BY_ID[id]
                    if (post != null) {
                        respond(
                            content = post,
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    } else {
                        respond(
                            content = """{"error":"not found"}""",
                            status = HttpStatusCode.NotFound,
                            headers = headersOf(HttpHeaders.ContentType, "application/json"),
                        )
                    }
                }
                else -> respond(content = "", status = HttpStatusCode.NotFound)
            }
        }

        return HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Test
    fun `getItems returns mapped items from API`() = runTest {
        val repository = ApiItemRepository(createClient())

        val items = repository.getItems()

        assertEquals(2, items.size)
        assertEquals("1", items[0].id)
        assertEquals("Test title", items[0].title)
        assertEquals("Test body content", items[0].subtitle)
    }

    @Test
    fun `getItem returns mapped item for valid id`() = runTest {
        val repository = ApiItemRepository(createClient())

        val item = repository.getItem("1")

        assertEquals("1", item?.id)
        assertEquals("Test title", item?.title)
    }

    @Test
    fun `getItem returns null for non-existent id`() = runTest {
        val repository = ApiItemRepository(createClient())

        val item = repository.getItem("999")

        assertNull(item)
    }

    @Test
    fun `getItems throws NetworkException on server error`() = runTest {
        val engine = MockEngine { respond(content = "", status = HttpStatusCode.InternalServerError) }
        val client = HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val repository = ApiItemRepository(client)

        assertFailsWith<CastociastoException.NetworkException> {
            repository.getItems()
        }
    }

    @Test
    fun `title first char is uppercased in mapping`() = runTest {
        val repository = ApiItemRepository(createClient())

        val items = repository.getItems()

        // PostDto.toItem() calls replaceFirstChar { it.uppercase() }
        assertEquals("Test title", items[0].title) // 'T' is already uppercase
        assertEquals("Another post", items[1].title) // 'a' -> 'A'
    }

    companion object {
        private val POSTS_JSON = """
            [
                {"userId": 1, "id": 1, "title": "test title", "body": "Test body content"},
                {"userId": 1, "id": 2, "title": "another post", "body": "Another\nbody"}
            ]
        """.trimIndent()

        private val POSTS_BY_ID = mapOf(
            "1" to """{"userId": 1, "id": 1, "title": "test title", "body": "Test body content"}""",
            "2" to """{"userId": 1, "id": 2, "title": "another post", "body": "Another body"}""",
        )
    }
}
