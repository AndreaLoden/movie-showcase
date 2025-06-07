package com.andrea.imdbshowcase.presentation.movies.state

import com.andrea.imdbshowcase.core.model.Movie

data class MoviesState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = listOf(),
    val error: String = ""
)
