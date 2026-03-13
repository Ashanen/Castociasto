package pl.rockit.castociasto.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import pl.rockit.castociasto.feature.categories.ui.CategoriesViewModel
import pl.rockit.castociasto.feature.favorites.ui.FavoritesViewModel
import pl.rockit.castociasto.feature.items.ui.DetailViewModel
import pl.rockit.castociasto.feature.items.ui.ListViewModel

object KoinHelper : KoinComponent {
    fun getListViewModel(): ListViewModel = get()
    fun getDetailViewModel(): DetailViewModel = get()
    fun getCategoriesViewModel(): CategoriesViewModel = get()
    fun getFavoritesViewModel(): FavoritesViewModel = get()
}
