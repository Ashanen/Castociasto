rootProject.name = "Castociasto"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")

// App (umbrella: iOS framework export, DI aggregation)
include(":shared:app")

// Foundation
include(":shared:foundation")

// Infrastructure
include(":shared:infrastructure:networking")

// Core (API contracts)
include(":shared:core:items")
include(":shared:core:categories")
include(":shared:core:favorites")

// Feature: Items
include(":shared:feature:items:domain")
include(":shared:feature:items:data")
include(":shared:feature:items:ui")

// Feature: Categories
include(":shared:feature:categories:domain")
include(":shared:feature:categories:data")
include(":shared:feature:categories:ui")

// Feature: Favorites
include(":shared:feature:favorites:domain")
include(":shared:feature:favorites:data")
include(":shared:feature:favorites:ui")