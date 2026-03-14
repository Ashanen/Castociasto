package pl.rockit.castociasto.e2e.config

object AppiumConfig {
    const val APPIUM_SERVER_URL = "http://127.0.0.1:4723"
    const val PLATFORM_NAME = "Android"
    const val AUTOMATION_NAME = "UiAutomator2"
    const val APP_PACKAGE = "pl.rockit.castociasto"
    const val APP_ACTIVITY = ".MainActivity"

    // Timeout settings (seconds)
    const val IMPLICIT_WAIT = 10L
    const val EXPLICIT_WAIT = 15L

    // Path to APK (relative to project root, set via env var or default)
    val apkPath: String
        get() = System.getenv("APK_PATH")
            ?: "../androidApp/build/outputs/apk/debug/androidApp-debug.apk"
}
