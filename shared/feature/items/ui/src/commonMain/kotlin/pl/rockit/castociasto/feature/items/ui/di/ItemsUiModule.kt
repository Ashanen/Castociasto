package pl.rockit.castociasto.feature.items.ui.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import pl.rockit.castociasto.feature.items.ui.DetailViewModel
import pl.rockit.castociasto.feature.items.ui.ListViewModel

val itemsUiModule = module {
    viewModelOf(::ListViewModel)
    viewModelOf(::DetailViewModel)
}
