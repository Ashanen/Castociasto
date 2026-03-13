package pl.rockit.castociasto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pl.rockit.castociasto.navigation.CastociastoNavHost
import pl.rockit.castociasto.ui.theme.CastociastoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            CastociastoTheme {
                CastociastoNavHost()
            }
        }
    }
}
