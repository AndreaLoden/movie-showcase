package com.andrea.tmdbshowcase.presentation.state

import com.andrea.tmdbshowcase.core.model.Movie

sealed class MovieGridUiState {
    data object Loading : MovieGridUiState()
    data class Success(val movies: List<Movie>, val todaysDate: String) : MovieGridUiState()
    data class Error(val message: String) : MovieGridUiState()
}
