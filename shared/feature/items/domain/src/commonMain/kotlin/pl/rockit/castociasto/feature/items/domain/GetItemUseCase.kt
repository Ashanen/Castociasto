package pl.rockit.castociasto.feature.items.domain

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.items.model.Item

fun interface GetItemUseCase {
    operator fun invoke(id: String): Flow<Item>
}
