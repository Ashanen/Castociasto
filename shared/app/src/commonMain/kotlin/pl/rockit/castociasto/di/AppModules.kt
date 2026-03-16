package pl.rockit.castociasto.di

import org.koin.dsl.module
import pl.rockit.castociasto.core.events.AppEventBus
import pl.rockit.castociasto.infrastructure.database.di.databaseModule
import pl.rockit.castociasto.feature.categories.data.di.categoriesDataModule
import pl.rockit.castociasto.feature.categories.domain.di.categoriesDomainModule
import pl.rockit.castociasto.feature.categories.ui.di.categoriesUiModule
import pl.rockit.castociasto.feature.favorites.data.di.favoritesDataModule
import pl.rockit.castociasto.feature.favorites.domain.di.favoritesDomainModule
import pl.rockit.castociasto.feature.favorites.ui.di.favoritesUiModule
import pl.rockit.castociasto.feature.items.data.di.itemsDataModule
import pl.rockit.castociasto.feature.items.domain.di.itemsDomainModule
import pl.rockit.castociasto.feature.items.ui.di.itemsUiModule
import pl.rockit.castociasto.infrastructure.networking.di.networkingModule

val eventsModule = module {
    single<AppEventBus> { AppEventBus() }
}

val appModules = listOf(
    // Infrastructure
    networkingModule,
    databaseModule,
    eventsModule,

    // Items feature
    itemsDataModule,
    itemsDomainModule,
    itemsUiModule,

    // Categories feature
    categoriesDataModule,
    categoriesDomainModule,
    categoriesUiModule,

    // Favorites feature
    favoritesDataModule,
    favoritesDomainModule,
    favoritesUiModule,
)
