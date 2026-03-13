package pl.rockit.castociasto.feature.favorites.ui.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import pl.rockit.castociasto.feature.favorites.ui.FavoritesViewModel

val favoritesUiModule = module {
    viewModelOf(::FavoritesViewModel)
}
