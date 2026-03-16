package pl.rockit.castociasto.e2e.config

enum class Platform {
    ANDROID,
    IOS;

    companion object {
        fun current(): Platform {
            val value = System.getProperty("platform")
                ?: System.getenv("PLATFORM")
                ?: "android"
            return when (value.lowercase()) {
                "ios" -> IOS
                else -> ANDROID
            }
        }
    }
}
