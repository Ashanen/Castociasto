package pl.rockit.castociasto.feature.items.domain.fake

import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository

class FakeItemRepository : ItemRepository {

    var items: List<Item> = emptyList()
    var itemById: Map<String, Item> = emptyMap()
    var shouldThrow: Throwable? = null

    override suspend fun getItems(): List<Item> {
        shouldThrow?.let { throw it }
        return items
    }

    override suspend fun getItem(id: String): Item? {
        shouldThrow?.let { throw it }
        return itemById[id]
    }
}
