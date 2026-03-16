package pl.rockit.castociasto.infrastructure.database.di

import android.content.Context
import androidx.room.Room
import org.koin.dsl.module
import pl.rockit.castociasto.infrastructure.database.CastociastoDatabase
import pl.rockit.castociasto.infrastructure.database.buildDatabase

actual val databaseModule = module {
    single {
        Room.databaseBuilder<CastociastoDatabase>(
            context = get<Context>(),
            name = get<Context>().getDatabasePath("castociasto.db").absolutePath,
        ).buildDatabase()
    }
    single { get<CastociastoDatabase>().itemDao() }
}
