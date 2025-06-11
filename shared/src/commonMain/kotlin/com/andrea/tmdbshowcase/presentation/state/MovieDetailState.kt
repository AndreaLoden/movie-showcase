package com.andrea.tmdbshowcase.presentation.state

import com.andrea.tmdbshowcase.core.model.Movie

sealed class MovieDetailState {
    data object Loading : MovieDetailState()
    data class Success(val movie: Movie) : MovieDetailState()
    data class Error(val message: String) : MovieDetailState()
}
