package pl.rockit.castociasto.infrastructure.database.di

import androidx.room.Room
import org.koin.dsl.module
import pl.rockit.castociasto.infrastructure.database.CastociastoDatabase
import pl.rockit.castociasto.infrastructure.database.buildDatabase
import platform.Foundation.NSHomeDirectory

actual val databaseModule = module {
    single {
        val dbPath = NSHomeDirectory() + "/Documents/castociasto.db"
        Room.databaseBuilder<CastociastoDatabase>(name = dbPath)
            .buildDatabase()
    }
    single { get<CastociastoDatabase>().itemDao() }
}
