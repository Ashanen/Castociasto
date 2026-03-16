package pl.rockit.castociasto.e2e.base

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import org.junit.After
import org.junit.Assume
import org.junit.Before
import pl.rockit.castociasto.e2e.config.AppiumConfig
import java.io.File
import java.net.URI

/**
 * Base class for all Appium E2E tests.
 *
 * Prerequisites:
 * 1. Appium server running: `appium`
 * 2. UiAutomator2 driver installed: `appium driver install uiautomator2`
 * 3. Android emulator or device connected: `adb devices`
 * 4. Debug APK built: `./gradlew :androidApp:assembleDebug`
 *
 * Run with: `./gradlew :e2e:test`
 * Or set APK_PATH env var: `APK_PATH=/path/to/app.apk ./gradlew :e2e:test`
 */
abstract class BaseE2ETest {

    protected lateinit var driver: AndroidDriver

    @Before
    fun setUp() {
        val options = UiAutomator2Options().apply {
            setPlatformName(AppiumConfig.PLATFORM_NAME)
            setAutomationName(AppiumConfig.AUTOMATION_NAME)
            setAppPackage(AppiumConfig.APP_PACKAGE)
            setAppActivity(AppiumConfig.APP_ACTIVITY)

            val apkFile = File(AppiumConfig.apkPath)
            if (apkFile.exists()) {
                setApp(apkFile.absolutePath)
            }

            setNoReset(true)

            // Compose testTag maps to raw resource-id (no package prefix)
            setCapability("appium:disableIdLocatorAutocompletion", true)

            // Always restart the app so tests start from the list screen
            setCapability("appium:forceAppLaunch", true)
        }

        try {
            driver = AndroidDriver(
                URI(AppiumConfig.APPIUM_SERVER_URL).toURL(),
                options,
            )
        } catch (e: Exception) {
            Assume.assumeNoException("Appium server not available, skipping E2E tests", e)
        }
    }

    @After
    fun tearDown() {
        if (::driver.isInitialized) {
            driver.quit()
        }
    }
}
