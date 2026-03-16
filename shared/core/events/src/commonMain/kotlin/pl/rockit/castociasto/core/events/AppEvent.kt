package pl.rockit.castociasto.core.events

sealed interface AppEvent {
    data class FavoriteToggled(val itemId: String) : AppEvent
    data object ItemsUpdated : AppEvent
}
