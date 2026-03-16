package pl.rockit.castociasto.infrastructure.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY title")
    fun observeAll(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun observeById(id: String): Flow<ItemEntity?>

    @Query("SELECT * FROM items ORDER BY title")
    suspend fun getAll(): List<ItemEntity>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getById(id: String): ItemEntity?

    @Upsert
    suspend fun upsertAll(items: List<ItemEntity>)

    @Query("DELETE FROM items")
    suspend fun deleteAll()
}
