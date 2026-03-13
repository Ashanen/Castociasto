package pl.rockit.castociasto.core.items.usecase

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.items.model.Item

fun interface GetItemUseCase {
    operator fun invoke(id: String): Flow<Item>
}
