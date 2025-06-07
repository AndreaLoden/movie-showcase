package com.andrea.imdbshowcase.presenttion.movies.state

data class PaginationState(
    val isLoading: Boolean = false,
    val skip: Int = 1,
    val endReached: Boolean = false
)
