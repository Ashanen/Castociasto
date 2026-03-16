package pl.rockit.castociasto.feature.items.domain

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository

internal class ObserveItemsUseCaseImpl(
    private val repository: ItemRepository,
) : ObserveItemsUseCase {
    override fun invoke(): Flow<List<Item>> = repository.observeItems()
}
