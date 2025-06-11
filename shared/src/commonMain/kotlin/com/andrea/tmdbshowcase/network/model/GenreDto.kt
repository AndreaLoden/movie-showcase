package com.andrea.tmdbshowcase.network.model

import com.andrea.tmdbshowcase.core.model.Genre
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreDto(

    @SerialName("id")
    val id: Int? = null,

    @SerialName("name")
    val name: String? = null
)

fun GenreDto.toGenre(): Genre {
    return Genre(
        id = id ?: -1,
        name = name ?: ""
    )
}
