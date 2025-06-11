package com.andrea.tmdbshowcase.presentation.state

import com.andrea.tmdbshowcase.core.model.Movie

sealed class MovieSearchState {
    data object Initial : MovieSearchState()
    data class Result(val movies: List<Movie>) : MovieSearchState()
    data object NoResults : MovieSearchState()
    data object Loading : MovieSearchState()
    data class Error(val message: String) : MovieSearchState()
}
