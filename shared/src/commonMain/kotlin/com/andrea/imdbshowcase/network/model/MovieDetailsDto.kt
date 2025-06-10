package com.vickbt.composeApp.data.network.models

import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.network.model.GenreDto
import com.andrea.imdbshowcase.network.model.SpokenLanguageDto
import com.andrea.imdbshowcase.network.model.toGenre
import com.andrea.imdbshowcase.network.model.toSpokenLanguage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailsDto(

    @SerialName("adult")
    val adult: Boolean? = null,

    @SerialName("backdrop_path")
    val backdropPath: String? = null,

    @SerialName("genres")
    val genres: List<GenreDto>? = null,

    @SerialName("homepage")
    val homepage: String? = null,

    @SerialName("id")
    val id: Int,

    @SerialName("imdb_id")
    val imdbId: String? = null,

    @SerialName("original_language")
    val originalLanguage: String? = null,

    @SerialName("original_title")
    val originalTitle: String,

    @SerialName("overview")
    val overview: String,

    @SerialName("popularity")
    val popularity: Double? = null,

    @SerialName("poster_path")
    val posterPath: String? = null,

    @SerialName("release_date")
    val releaseDate: String? = null,

    @SerialName("runtime")
    val runtime: Int? = null,

    @SerialName("spoken_languages")
    val spokenLanguages: List<SpokenLanguageDto>? = null,

    @SerialName("status")
    val status: String? = null,

    @SerialName("tagline")
    val tagline: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("video")
    val video: Boolean? = null,

    @SerialName("vote_average")
    val voteAverage: Double? = null,

    @SerialName("vote_count")
    val voteCount: Int? = null
)

fun MovieDetailsDto.toMovie(): Movie {
    return Movie(
        id = id.toString(),
        imgURL = posterPath?.let { "https://image.tmdb.org/t/p/w500/$it" } ?: "",
        title = title ?: "",
        tagline = tagline ?: "",
        overview = overview,
        genres = genres?.map { it.toGenre() } ?: listOf(),
        runtime = runtime ?: -1,
        spokenLanguages = spokenLanguages?.map { it.toSpokenLanguage() } ?: listOf(),
        voteAverage = voteAverage ?: 1.0,
        releaseDate = releaseDate ?: ""
    )
}
