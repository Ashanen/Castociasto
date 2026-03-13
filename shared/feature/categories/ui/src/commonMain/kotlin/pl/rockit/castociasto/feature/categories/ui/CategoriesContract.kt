package pl.rockit.castociasto.feature.categories.ui

import pl.rockit.castociasto.core.categories.model.Category

data class CategoriesState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed interface CategoriesAction {
    data object LoadCategories : CategoriesAction
    data class CategoryClicked(val categoryId: String) : CategoriesAction
}

sealed interface CategoriesSideEffect {
    data class NavigateToCategory(val categoryId: String) : CategoriesSideEffect
}
