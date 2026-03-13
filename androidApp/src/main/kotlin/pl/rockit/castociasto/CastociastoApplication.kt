package pl.rockit.castociasto

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.rockit.castociasto.di.appModules

class CastociastoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CastociastoApplication)
            modules(appModules)
        }
    }
}
