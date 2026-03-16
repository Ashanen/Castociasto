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
            ?: "../iosApp/build/Build/Products/Debug-iphonesimulator/iosApp.app"

    val iosDeviceName: String
        get() = System.getenv("IOS_DEVICE_NAME") ?: "iPhone 16"

    val iosPlatformVersion: String
        get() = System.getenv("IOS_PLATFORM_VERSION") ?: "18.0"
}
