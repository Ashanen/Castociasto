package pl.rockit.castociasto.feature.favorites.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FakeFavoriteRepositoryTest {

    private val repository = FakeFavoriteRepository()

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
        // Snapshot should not be affected by subsequent mutations
        assertTrue(snapshot.contains("1"))
    }
}
