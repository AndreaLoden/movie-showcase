package com.andrea.imdbshowcase.presentation.state

data class MovieGridPaginationState(
    val isLoading: Boolean = false,
    val page: Int = 1,
    val endReached: Boolean = false
)
