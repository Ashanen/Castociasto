package pl.rockit.castociasto.feature.favorites.data

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.test.runTest
import pl.rockit.castociasto.core.events.AppEvent
import pl.rockit.castociasto.core.events.AppEventBus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class FakeFavoriteRepositoryTest {

    private val eventBus = FakeAppEventBus()
    private val repository = FakeFavoriteRepository(eventBus)

    @Test
    fun `initial favorites contain items 1 and 3`() = runTest {
        val ids = repository.getFavoriteIds()
        assertEquals(setOf("1", "3"), ids)
    }

    @Test
    fun `isFavorite returns true for initially favorited item`() = runTest {
        assertTrue(repository.isFavorite("1"))
        assertTrue(repository.isFavorite("3"))
    }

    @Test
    fun `isFavorite returns false for non-favorited item`() = runTest {
        assertFalse(repository.isFavorite("2"))
        assertFalse(repository.isFavorite("99"))
    }

    @Test
    fun `toggleFavorite adds item when not favorited`() = runTest {
        assertFalse(repository.isFavorite("5"))

        repository.toggleFavorite("5")

        assertTrue(repository.isFavorite("5"))
        assertTrue(repository.getFavoriteIds().contains("5"))
    }

    @Test
    fun `toggleFavorite removes item when already favorited`() = runTest {
        assertTrue(repository.isFavorite("1"))

        repository.toggleFavorite("1")

        assertFalse(repository.isFavorite("1"))
        assertFalse(repository.getFavoriteIds().contains("1"))
    }

    @Test
    fun `toggle twice returns to original state`() = runTest {
        val wasFavorite = repository.isFavorite("1")

        repository.toggleFavorite("1")
        repository.toggleFavorite("1")

        assertEquals(wasFavorite, repository.isFavorite("1"))
    }

    @Test
    fun `getFavoriteIds returns snapshot not live reference`() = runTest {
        val snapshot = repository.getFavoriteIds()
        repository.toggleFavorite("1")
        assertTrue(snapshot.contains("1"))
    }

    @Test
    fun `toggleFavorite emits FavoriteToggled event`() = runTest {
        eventBus.events.test {
            repository.toggleFavorite("5")
            val event = awaitItem()
            assertIs<AppEvent.FavoriteToggled>(event)
            assertEquals("5", event.itemId)
        }
    }
}

private class FakeAppEventBus : AppEventBus {
    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 64)
    override val events: SharedFlow<AppEvent> = _events.asSharedFlow()
    override suspend fun emit(event: AppEvent) { _events.emit(event) }
}
