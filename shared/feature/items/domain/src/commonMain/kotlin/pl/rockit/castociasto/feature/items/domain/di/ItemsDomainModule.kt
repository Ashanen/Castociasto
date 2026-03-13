package pl.rockit.castociasto.feature.items.domain.di

import org.koin.dsl.module
import pl.rockit.castociasto.core.items.usecase.GetItemUseCase
import pl.rockit.castociasto.core.items.usecase.GetItemsUseCase
import pl.rockit.castociasto.feature.items.domain.GetItemUseCaseImpl
import pl.rockit.castociasto.feature.items.domain.GetItemsUseCaseImpl

val itemsDomainModule = module {
    factory<GetItemsUseCase> { GetItemsUseCaseImpl(get()) }
    factory<GetItemUseCase> { GetItemUseCaseImpl(get(), get()) }
}
