package com.andrea.tmdbshowcase.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResultsDto(
    @SerialName("page")
    val page: Int? = null,

    @SerialName("results")
    val movies: List<MovieDto> = listOf(),

    @SerialName("total_pages")
    val totalPages: Int? = null,

    @SerialName("total_results")
    val totalResults: Int? = null
)
