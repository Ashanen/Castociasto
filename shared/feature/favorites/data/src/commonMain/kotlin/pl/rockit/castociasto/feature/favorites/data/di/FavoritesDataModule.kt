package pl.rockit.castociasto.feature.favorites.data.di

import org.koin.dsl.module
import pl.rockit.castociasto.core.favorites.repository.FavoriteRepository
import pl.rockit.castociasto.feature.favorites.data.FakeFavoriteRepository

val favoritesDataModule = module {
    single<FavoriteRepository> { FakeFavoriteRepository() }
}
