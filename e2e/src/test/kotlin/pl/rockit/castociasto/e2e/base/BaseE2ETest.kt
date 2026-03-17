package pl.rockit.castociasto.e2e.base

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import io.appium.java_client.ios.IOSDriver
import io.appium.java_client.ios.options.XCUITestOptions
import org.junit.After
import org.junit.Assume
import org.junit.Before
import pl.rockit.castociasto.e2e.config.AppiumConfig
import pl.rockit.castociasto.e2e.config.Platform
import java.io.File
import java.net.URI

/**
 * Base class for all Appium E2E tests.
 * Supports Android and iOS — select via PLATFORM env var or -Dplatform system property.
 *
 * Prerequisites:
 * 1. Appium server running: `appium`
 * 2. Driver installed: `appium driver install uiautomator2` (Android) or `appium driver install xcuitest` (iOS)
 * 3. Emulator/simulator or device connected
 * 4. App built: `./gradlew :androidApp:assembleDebug` (Android) or `xcodebuild ...` (iOS)
 */
abstract class BaseE2ETest {

    protected lateinit var driver: AppiumDriver

    @Before
    fun setUp() {
        try {
            driver = when (Platform.current()) {
                Platform.ANDROID -> createAndroidDriver()
                Platform.IOS -> createIOSDriver()
            }
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

    private fun createAndroidDriver(): AndroidDriver {
        val options = UiAutomator2Options().apply {
            setPlatformName(AppiumConfig.ANDROID_PLATFORM_NAME)
            setAutomationName(AppiumConfig.ANDROID_AUTOMATION_NAME)
            setAppPackage(AppiumConfig.APP_PACKAGE)
            setAppActivity(AppiumConfig.APP_ACTIVITY)

            val apkFile = File(AppiumConfig.apkPath)
            if (apkFile.exists()) {
                setApp(apkFile.absolutePath)
            }

            setNoReset(true)
            setCapability("appium:disableIdLocatorAutocompletion", true)
            setCapability("appium:forceAppLaunch", true)
        }
        return AndroidDriver(URI(AppiumConfig.APPIUM_SERVER_URL).toURL(), options)
    }

    private fun createIOSDriver(): IOSDriver {
        val options = XCUITestOptions().apply {
            setPlatformName(AppiumConfig.IOS_PLATFORM_NAME)
            setAutomationName(AppiumConfig.IOS_AUTOMATION_NAME)
            setDeviceName(AppiumConfig.iosDeviceName)
            setPlatformVersion(AppiumConfig.iosPlatformVersion)

            val appFile = File(AppiumConfig.appPath)
            if (appFile.exists()) {
                setApp(appFile.absolutePath)
            } else {
                setBundleId(AppiumConfig.IOS_BUNDLE_ID)
            }

            setNoReset(false)
            setCapability("appium:forceAppLaunch", true)
        }
        return IOSDriver(URI(AppiumConfig.APPIUM_SERVER_URL).toURL(), options)
    }
}
