package pl.rockit.castociasto.core.items.repository

import pl.rockit.castociasto.core.items.model.Item

interface ItemRepository {
    suspend fun getItems(): List<Item>
    suspend fun getItem(id: String): Item?
}
