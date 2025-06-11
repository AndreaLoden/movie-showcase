package com.andrea.imdbshowcase.presentation.state

import com.andrea.imdbshowcase.core.model.Movie

sealed class MovieSearchState {
    data object Initial : MovieSearchState()
    data class Result(val movies: List<Movie>) : MovieSearchState()
    data object NoResults : MovieSearchState()
    data object Loading : MovieSearchState()
    data object Refresh : MovieSearchState()
    data class Error(val message: String) : MovieSearchState()
}
