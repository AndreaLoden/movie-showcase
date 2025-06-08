package com.andrea.imdbshowcase.core.model

data class Movie(
    val id: String,
    val imgURL: String,
    val title: String,
    val backdrop_path: String?,
    val tagline: String,
    val overview: String,
    val genres: List<Genre>,
    val runtime: Int,
    val spoken_languages: List<SpokenLanguage>,
    val vote_average: Double,
    val release_date: String
)

data class Genre(val id: Int, val name: String)

data class SpokenLanguage(val english_name: String)
