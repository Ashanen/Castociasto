package pl.rockit.castociasto.feature.items.data.dto

import kotlinx.serialization.Serializable
import pl.rockit.castociasto.core.items.model.Item

@Serializable
internal data class PostDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String,
)

internal fun PostDto.toItem() = Item(
    id = id.toString(),
    title = title.replaceFirstChar { it.uppercase() },
    subtitle = body.replace("\n", " "),
)
