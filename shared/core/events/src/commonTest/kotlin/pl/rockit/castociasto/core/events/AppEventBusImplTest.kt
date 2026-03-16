package pl.rockit.castociasto.core.events

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AppEventBusImplTest {

    private val eventBus = AppEventBusImpl()

    @Test
    fun `emitted event is received by subscriber`() = runTest {
        eventBus.events.test {
            eventBus.emit(AppEvent.FavoriteToggled("42"))
            val event = awaitItem()
            assertIs<AppEvent.FavoriteToggled>(event)
            assertEquals("42", event.itemId)
        }
    }

    @Test
    fun `ItemsUpdated event is received`() = runTest {
        eventBus.events.test {
            eventBus.emit(AppEvent.ItemsUpdated)
            val event = awaitItem()
            assertIs<AppEvent.ItemsUpdated>(event)
        }
    }

    @Test
    fun `multiple events are received in order`() = runTest {
        eventBus.events.test {
            eventBus.emit(AppEvent.FavoriteToggled("1"))
            eventBus.emit(AppEvent.ItemsUpdated)
            eventBus.emit(AppEvent.FavoriteToggled("2"))

            assertIs<AppEvent.FavoriteToggled>(awaitItem())
            assertIs<AppEvent.ItemsUpdated>(awaitItem())
            assertIs<AppEvent.FavoriteToggled>(awaitItem())
        }
    }
}
