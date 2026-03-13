package pl.rockit.castociasto.feature.categories.ui.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import pl.rockit.castociasto.feature.categories.ui.CategoriesViewModel

val categoriesUiModule = module {
    viewModelOf(::CategoriesViewModel)
}
