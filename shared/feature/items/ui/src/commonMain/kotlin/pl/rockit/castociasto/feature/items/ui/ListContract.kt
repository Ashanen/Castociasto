package pl.rockit.castociasto.feature.items.ui

import pl.rockit.castociasto.core.items.model.Item

data class ListState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface ListAction {
    data object Refresh : ListAction
    data class ItemClicked(val itemId: String) : ListAction
}

sealed interface ListSideEffect {
    data class NavigateToDetail(val itemId: String) : ListSideEffect
}
