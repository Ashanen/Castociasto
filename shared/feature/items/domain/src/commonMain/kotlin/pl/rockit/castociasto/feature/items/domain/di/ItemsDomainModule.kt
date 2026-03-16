package pl.rockit.castociasto.feature.items.domain.di

import org.koin.dsl.module
import pl.rockit.castociasto.feature.items.domain.GetItemUseCase
import pl.rockit.castociasto.feature.items.domain.GetItemUseCaseImpl
import pl.rockit.castociasto.feature.items.domain.GetItemsUseCase
import pl.rockit.castociasto.feature.items.domain.GetItemsUseCaseImpl
import pl.rockit.castociasto.feature.items.domain.ObserveItemsUseCase
import pl.rockit.castociasto.feature.items.domain.ObserveItemsUseCaseImpl
import pl.rockit.castociasto.feature.items.domain.RefreshItemsUseCase
import pl.rockit.castociasto.feature.items.domain.RefreshItemsUseCaseImpl

val itemsDomainModule = module {
    factory<GetItemsUseCase> { GetItemsUseCaseImpl(get()) }
    factory<GetItemUseCase> { GetItemUseCaseImpl(get(), get()) }
    factory<ObserveItemsUseCase> { ObserveItemsUseCaseImpl(get()) }
    factory<RefreshItemsUseCase> { RefreshItemsUseCaseImpl(get()) }
}
