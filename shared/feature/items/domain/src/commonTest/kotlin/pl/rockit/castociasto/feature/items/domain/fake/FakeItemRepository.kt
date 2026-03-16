package pl.rockit.castociasto.feature.items.domain.fake

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import pl.rockit.castociasto.core.items.model.Item
import pl.rockit.castociasto.core.items.repository.ItemRepository

class FakeItemRepository : ItemRepository {

    var items: List<Item> = emptyList()
    var itemById: Map<String, Item> = emptyMap()
    var shouldThrow: Throwable? = null

    private val _itemsFlow = MutableStateFlow<List<Item>>(emptyList())
    private val _itemByIdFlow = MutableStateFlow<Map<String, Item>>(emptyMap())
    var refreshCount = 0
        private set

    override suspend fun getItems(): List<Item> {
        shouldThrow?.let { throw it }
        return items
    }

    override suspend fun getItem(id: String): Item? {
        shouldThrow?.let { throw it }
        return itemById[id]
    }

    override fun observeItems(): Flow<List<Item>> = _itemsFlow

    override fun observeItem(id: String): Flow<Item?> =
        _itemByIdFlow.map { it[id] }

    override suspend fun refresh() {
        shouldThrow?.let { throw it }
        refreshCount++
        _itemsFlow.value = items
        _itemByIdFlow.value = itemById
    }

    fun emitItems(items: List<Item>) {
        _itemsFlow.value = items
    }
}
