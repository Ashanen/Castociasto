package pl.rockit.castociasto.feature.categories.data.di

import org.koin.dsl.module
import pl.rockit.castociasto.core.categories.repository.CategoryRepository
import pl.rockit.castociasto.feature.categories.data.FakeCategoryRepository

val categoriesDataModule = module {
    single<CategoryRepository> { FakeCategoryRepository() }
}
