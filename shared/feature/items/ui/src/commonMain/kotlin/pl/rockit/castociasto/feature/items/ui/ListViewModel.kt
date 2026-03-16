package pl.rockit.castociasto.feature.items.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import pl.rockit.castociasto.feature.items.domain.GetItemsUseCase
import pl.rockit.castociasto.foundation.BaseViewModel
import pl.rockit.castociasto.foundation.extensions.launchWith

class ListViewModel(
    private val getItems: GetItemsUseCase,
) : BaseViewModel<ListState, ListAction, ListSideEffect>() {

    override val _uiState = MutableStateFlow(ListState())

    init {
        onAction(ListAction.LoadItems)
    }

    override fun onAction(action: ListAction) {
        when (action) {
            is ListAction.LoadItems -> loadItems()
            is ListAction.ItemClicked -> sendEffect(ListSideEffect.NavigateToDetail(action.itemId))
        }
    }

    private fun loadItems() {
        getItems()
            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
            .onEach { items ->
                _uiState.update { it.copy(items = items, isLoading = false) }
            }
            .launchWith(viewModelScope) { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
    }
}
