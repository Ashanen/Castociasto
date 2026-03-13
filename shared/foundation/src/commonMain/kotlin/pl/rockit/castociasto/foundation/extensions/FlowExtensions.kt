package pl.rockit.castociasto.foundation.extensions

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn

fun <T> flowSingle(block: suspend () -> T): Flow<T> = flow {
    emit(block())
}

fun <T> Flow<T>.launchWith(
    scope: CoroutineScope,
    onError: (Throwable) -> Unit,
): Flow<T> {
    this.catch { error ->
        if (error is CancellationException) throw error
        onError(error)
    }.launchIn(scope)
    return this
}
