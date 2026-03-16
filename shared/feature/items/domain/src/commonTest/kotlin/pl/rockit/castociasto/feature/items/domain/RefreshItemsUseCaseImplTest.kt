package pl.rockit.castociasto.feature.items.domain

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import pl.rockit.castociasto.feature.items.domain.fake.FakeItemRepository
import pl.rockit.castociasto.foundation.exception.CastociastoException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class RefreshItemsUseCaseImplTest {

    private val repository = FakeItemRepository()
    private val useCase = RefreshItemsUseCaseImpl(repository)

    @Test
    fun `calls repository refresh`() = runTest {
        useCase().test {
            awaitItem()
            awaitComplete()
        }
        assertEquals(1, repository.refreshCount)
    }

    @Test
    fun `propagates repository exception`() = runTest {
        repository.shouldThrow = CastociastoException.NetworkException.NoConnection("No internet")

        useCase().test {
            val error = awaitError()
            assertIs<CastociastoException.NetworkException.NoConnection>(error)
        }
    }
}
