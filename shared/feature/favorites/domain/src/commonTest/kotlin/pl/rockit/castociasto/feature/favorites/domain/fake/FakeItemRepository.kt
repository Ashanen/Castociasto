package pl.rockit.castociasto.feature.favorites.domain.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository

class FakeItemRepository : ItemRepository {
    var itemById: Map<String, Item> = emptyMap()

    override suspend fun getItems(): List<Item> = itemById.values.toList()
    override suspend fun getItem(id: String): Item? = itemById[id]
    override fun observeItems(): Flow<List<Item>> = emptyFlow()
    override fun observeItem(id: String): Flow<Item?> = emptyFlow()
    override suspend fun refresh() {}
}
