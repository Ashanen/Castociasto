package pl.rockit.castociasto.core.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class AppEventBusImpl : AppEventBus {

    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 64)

    override val events: SharedFlow<AppEvent> = _events.asSharedFlow()

    override suspend fun emit(event: AppEvent) {
        _events.emit(event)
    }
}
