package pl.rockit.castociasto.feature.favorites.domain.di

import org.koin.dsl.module
import pl.rockit.castociasto.core.favorites.usecase.GetFavoritesUseCase
import pl.rockit.castociasto.core.favorites.usecase.ToggleFavoriteUseCase
import pl.rockit.castociasto.feature.favorites.domain.GetFavoritesUseCaseImpl
import pl.rockit.castociasto.feature.favorites.domain.ToggleFavoriteUseCaseImpl

val favoritesDomainModule = module {
    factory<GetFavoritesUseCase> { GetFavoritesUseCaseImpl(get(), get()) }
    factory<ToggleFavoriteUseCase> { ToggleFavoriteUseCaseImpl(get(), get()) }
}
