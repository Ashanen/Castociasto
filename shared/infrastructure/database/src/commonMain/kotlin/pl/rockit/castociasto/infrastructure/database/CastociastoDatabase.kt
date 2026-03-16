package pl.rockit.castociasto.infrastructure.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [ItemEntity::class], version = 1)
@ConstructedBy(CastociastoDatabaseConstructor::class)
abstract class CastociastoDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object CastociastoDatabaseConstructor : RoomDatabaseConstructor<CastociastoDatabase>
