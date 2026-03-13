package pl.rockit.castociasto.feature.items.data.di

import org.koin.dsl.module
import pl.rockit.castociasto.core.items.repository.ItemRepository
import pl.rockit.castociasto.feature.items.data.ApiItemRepository

val itemsDataModule = module {
    single<ItemRepository> { ApiItemRepository(get()) }
}
