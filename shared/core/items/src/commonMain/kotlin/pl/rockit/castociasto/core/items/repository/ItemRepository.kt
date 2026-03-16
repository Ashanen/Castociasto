package pl.rockit.castociasto.core.items.repository

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.items.model.Item

interface ItemRepository {
    suspend fun getItems(): List<Item>
    suspend fun getItem(id: String): Item?
    fun observeItems(): Flow<List<Item>>
    fun observeItem(id: String): Flow<Item?>
    suspend fun refresh()
}
