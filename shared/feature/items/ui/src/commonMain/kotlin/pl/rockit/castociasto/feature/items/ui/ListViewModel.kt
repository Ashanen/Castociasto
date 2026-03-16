package pl.rockit.castociasto.feature.items.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import pl.rockit.castociasto.feature.items.domain.ObserveItemsUseCase
import pl.rockit.castociasto.feature.items.domain.RefreshItemsUseCase
import pl.rockit.castociasto.foundation.BaseViewModel
import pl.rockit.castociasto.foundation.extensions.launchWith

class ListViewModel(
    private val observeItems: ObserveItemsUseCase,
    private val refreshItems: RefreshItemsUseCase,
) : BaseViewModel<ListState, ListAction, ListSideEffect>() {

    override val _uiState = MutableStateFlow(ListState())

    init {
        startObserving()
        onAction(ListAction.Refresh)
    }

    override fun onAction(action: ListAction) {
        when (action) {
            is ListAction.Refresh -> refresh()
            is ListAction.ItemClicked -> sendEffect(ListSideEffect.NavigateToDetail(action.itemId))
        }
    }

    private fun startObserving() {
        observeItems()
            .onEach { items ->
                _uiState.update { it.copy(items = items) }
            }
            .launchWith(viewModelScope) { error ->
                _uiState.update { it.copy(error = error.message) }
            }
    }

    private fun refresh() {
        refreshItems()
            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
            .onEach { _uiState.update { it.copy(isLoading = false) } }
            .launchWith(viewModelScope) { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
    }
}
