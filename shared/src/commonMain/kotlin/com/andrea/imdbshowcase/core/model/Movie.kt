package com.andrea.imdbshowcase.core.model

data class Movie(
    val id: String,
    val imgURL: String,
    val title: String,
    val tagline: String,
    val overview: String,
    val genres: List<Genre>,
    val runtime: Int,
    val spokenLanguages: List<SpokenLanguage>,
    val voteAverage: Double,
    val releaseDate: String
)

data class Genre(val id: Int, val name: String)

data class SpokenLanguage(val englishName: String)
