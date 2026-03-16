package pl.rockit.castociasto.feature.items.domain

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository
import pl.rockit.castociasto.feature.items.domain.GetItemsUseCase
import pl.rockit.castociasto.foundation.extensions.flowSingle

internal class GetItemsUseCaseImpl(
    private val repository: ItemRepository,
) : GetItemsUseCase {
    override fun invoke(): Flow<List<Item>> = flowSingle {
        val items = repository.getItems()

        items.sortedBy { it.title }
    }
}
