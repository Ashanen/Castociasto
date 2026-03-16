package pl.rockit.castociasto.feature.items.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class PostDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String,
)
