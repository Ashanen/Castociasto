package pl.rockit.castociasto.feature.items.ui

import pl.rockit.castociasto.core.items.model.Item

data class DetailState(
    val item: Item? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface DetailAction {
    data class LoadItem(val id: String) : DetailAction
    data object GoBack : DetailAction
}

sealed interface DetailSideEffect {
    data object NavigateBack : DetailSideEffect
}
