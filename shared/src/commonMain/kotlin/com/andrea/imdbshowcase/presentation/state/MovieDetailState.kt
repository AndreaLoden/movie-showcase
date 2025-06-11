package com.andrea.imdbshowcase.presentation.state

import com.andrea.imdbshowcase.core.model.Movie

data class MovieDetailState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val error: String = ""
)
