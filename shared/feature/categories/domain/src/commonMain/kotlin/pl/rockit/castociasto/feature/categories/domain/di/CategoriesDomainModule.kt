package pl.rockit.castociasto.feature.categories.domain.di

import org.koin.dsl.module
import pl.rockit.castociasto.feature.categories.domain.GetCategoriesUseCase
import pl.rockit.castociasto.feature.categories.domain.GetCategoriesUseCaseImpl

val categoriesDomainModule = module {
    factory<GetCategoriesUseCase> { GetCategoriesUseCaseImpl(get()) }
}
