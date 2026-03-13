package pl.rockit.castociasto.feature.items.ui

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import pl.rockit.castociasto.core.items.usecase.GetItemUseCase
import pl.rockit.castociasto.foundation.BaseViewModel
import pl.rockit.castociasto.foundation.extensions.launchWith

class DetailViewModel(
    private val getItem: GetItemUseCase,
) : BaseViewModel<DetailState, DetailAction, DetailSideEffect>() {

    override val _uiState = MutableStateFlow(DetailState())

    override fun onAction(action: DetailAction) {
        when (action) {
            is DetailAction.LoadItem -> loadItem(action.id)
            is DetailAction.GoBack -> sendEffect(DetailSideEffect.NavigateBack)
        }
    }

    private fun loadItem(id: String) {
        getItem(id)
            .onStart { _uiState.update { it.copy(isLoading = true, error = null) } }
            .onEach { item ->
                _uiState.update { it.copy(item = item, isLoading = false) }
            }
            .launchWith(viewModelScope) { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
    }
}
