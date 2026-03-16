package pl.rockit.castociasto.core.events

import kotlinx.coroutines.flow.SharedFlow

interface AppEventBus {
    val events: SharedFlow<AppEvent>
    suspend fun emit(event: AppEvent)

    companion object {
        operator fun invoke(): AppEventBus = AppEventBusImpl()
    }
}
