package pl.rockit.castociasto.foundation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel<S : Any, A, E> : ViewModel() {
    protected abstract val _uiState: MutableStateFlow<S>
    val uiState: StateFlow<S> by lazy { _uiState.asStateFlow() }

    private val _sideEffects = Channel<E>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    abstract fun onAction(action: A)

    protected fun sendEffect(effect: E) {
        _sideEffects.trySend(effect)
    }
}
