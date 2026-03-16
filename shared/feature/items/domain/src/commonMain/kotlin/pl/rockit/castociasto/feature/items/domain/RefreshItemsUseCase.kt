package pl.rockit.castociasto.feature.items.domain

import kotlinx.coroutines.flow.Flow

fun interface RefreshItemsUseCase {
    operator fun invoke(): Flow<Unit>
}
