package pl.rockit.castociasto.e2e.config

object AppiumConfig {
    const val APPIUM_SERVER_URL = "http://127.0.0.1:4723"

    // Timeout settings (seconds)
    const val EXPLICIT_WAIT = 15L

    // Android
    const val ANDROID_PLATFORM_NAME = "Android"
    const val ANDROID_AUTOMATION_NAME = "UiAutomator2"
    const val APP_PACKAGE = "pl.rockit.castociasto"
    const val APP_ACTIVITY = ".MainActivity"

    val apkPath: String
        get() = System.getenv("APK_PATH")
            ?: "../androidApp/build/outputs/apk/debug/androidApp-debug.apk"

    // iOS
    const val IOS_PLATFORM_NAME = "iOS"
    const val IOS_AUTOMATION_NAME = "XCUITest"
    const val IOS_BUNDLE_ID = "pl.rockit.castociasto"

    val appPath: String
        get() = System.getenv("APP_PATH")
            ?: "../iosApp/build/Build/Products/Debug-iphonesimulator/Castociasto.app"

    val iosDeviceName: String
        get() = System.getenv("IOS_DEVICE_NAME") ?: detectBootedSimulatorName()

    val iosPlatformVersion: String
        get() = System.getenv("IOS_PLATFORM_VERSION") ?: detectBootedSimulatorVersion()

    private fun detectBootedSimulatorVersion(): String {
        return runSimctlCommand(
            parseRegex = Regex("""--\s+iOS\s+([\d.]+)\s+--"""),
            errorMessage = "Could not detect iOS simulator version. Boot a simulator or set IOS_PLATFORM_VERSION env var.",
        )
    }

    private fun detectBootedSimulatorName(): String {
        return runSimctlCommand(
            parseRegex = Regex("""^\s+(.+?)\s+\([A-F0-9-]+\)\s+\(Booted\)""", RegexOption.MULTILINE),
            errorMessage = "Could not detect booted simulator name. Boot a simulator or set IOS_DEVICE_NAME env var.",
        )
    }

    private fun runSimctlCommand(parseRegex: Regex, errorMessage: String): String {
        val process = ProcessBuilder("xcrun", "simctl", "list", "devices", "booted")
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()
        return parseRegex.find(output)?.groupValues?.get(1)
            ?: error(errorMessage)
    }
}
