package pl.rockit.castociasto.core.items.model

data class Item(
    val id: String,
    val title: String,
    val subtitle: String,
    val isFavorite: Boolean = false,
)
