package com.andrea.imdbshowcase.network.model

import com.andrea.imdbshowcase.core.model.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    @SerialName("adult")
    val adult: Boolean? = null,

    @SerialName("backdrop_path")
    val backdropPath: String? = null,

    @SerialName("genre_ids")
    val genreIds: List<Int>? = null,

    @SerialName("id")
    val id: Int,

    @SerialName("original_language")
    val originalLanguage: String? = null,

    @SerialName("original_title")
    val originalTitle: String? = null,

    @SerialName("overview")
    val overview: String? = null,

    @SerialName("popularity")
    val popularity: Double? = null,

    @SerialName("poster_path")
    val posterPath: String? = null,

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("video")
    val video: Boolean? = null,

    @SerialName("vote_average")
    val voteAverage: Double? = null,

    @SerialName("vote_count")
    val voteCount: Int? = null,

    @SerialName("media_type")
    val mediaType: String? = null

)

fun MovieDto.toMovie(): Movie {
    return Movie(
        id = id.toString(),
        imgURL = posterPath?.let { "https://image.tmdb.org/t/p/w500/$it" } ?: "",
        title = title ?: "",
        tagline = "",
        overview = overview ?: "",
        genres = listOf(),
        runtime = -1,
        spokenLanguages = listOf(),
        voteAverage = voteAverage ?: 1.0,
        releaseDate = releaseDate ?: ""
    )
}
