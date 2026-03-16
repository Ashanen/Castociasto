package pl.rockit.castociasto.feature.items.domain

import kotlinx.coroutines.flow.Flow
import pl.rockit.castociasto.core.items.repository.ItemRepository
import pl.rockit.castociasto.foundation.extensions.flowSingle

internal class RefreshItemsUseCaseImpl(
    private val repository: ItemRepository,
) : RefreshItemsUseCase {
    override fun invoke(): Flow<Unit> = flowSingle {
        repository.refresh()
    }
}
