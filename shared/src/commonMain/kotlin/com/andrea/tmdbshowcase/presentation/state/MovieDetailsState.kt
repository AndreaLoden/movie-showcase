package com.andrea.tmdbshowcase.presentation.state

import com.andrea.tmdbshowcase.core.model.Movie

data class MovieDetailsState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val error: String = ""
)
