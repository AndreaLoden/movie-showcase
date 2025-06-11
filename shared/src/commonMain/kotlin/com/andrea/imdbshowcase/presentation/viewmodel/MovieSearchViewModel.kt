package com.andrea.imdbshowcase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.presentation.state.MovieSearchState
import com.andrea.imdbshowcase.presentation.state.PaginationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MovieSearchViewModel(
    private val movieRepository: MovieRepository,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : ViewModel() {

    private val searchMovieResultsMutable =
        MutableStateFlow<MovieSearchState>(MovieSearchState.Initial)
    val searchMovieResults = searchMovieResultsMutable.asStateFlow()

    private val paginationStateMutable = MutableStateFlow(PaginationState())
    val paginationState = paginationStateMutable.asStateFlow()

    private val userInput = MutableStateFlow("")

    init {
        viewModelScope.launch {
            userInput
                .onEach {
                    if (it.isNotBlank()) {
                        onRequestLoading()
                    }
                }
                .debounce(1000)
                .distinctUntilChanged()
                .collectLatest { query ->
                    handleQuery(query)
                }
        }
    }

    fun onNewSearchQuery(query: String){
        userInput.update { query }
    }

    fun getMoviesPaginated(query: String) {
        with(searchMovieResultsMutable.value) {
            if (this is MovieSearchState.Result && movies.isEmpty()) {
                return
            }
        }

        if (paginationStateMutable.value.endReached) {
            return
        }

        getMovies(query, paginationStateMutable.value.page)
    }

    private fun handleQuery(query: String) {
        when {
            query.isBlank() -> {
                searchMovieResultsMutable.update {
                    MovieSearchState.Initial
                }

                paginationStateMutable.update {
                    it.copy(
                        page = 1,
                        endReached = true,
                        isLoading = false
                    )
                }
            }

            else -> {
                getMovies(query, 1)
            }
        }
    }

    private fun getMovies(
        query: String,
        page: Int = 1
    ) {
        scope.launch {
            movieRepository.getMoviesForQueryRemote(query, page)
                .distinctUntilChanged()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> result.data?.let { data ->
                            onRequestSuccess(page, data)
                        }

                        is Resource.Error -> onRequestError(result.message)
                        is Resource.Loading -> {
                            //don't do anything, loading is already shown
                        }
                    }
                }
        }
    }

    private fun onRequestSuccess(
        page: Int,
        data: List<Movie>
    ) {
        val currentState = searchMovieResultsMutable.value

        val updatedMovies = if (page > 1 && currentState is MovieSearchState.Result) {
            currentState.movies + data
        } else {
            data
        }

        searchMovieResultsMutable.value =
            if (updatedMovies.isEmpty()) {
                MovieSearchState.NoResults
            } else {
                MovieSearchState.Result(updatedMovies)
            }

        paginationStateMutable.update {
            it.copy(
                page = it.page + 1,
                endReached = data.isEmpty(),
                isLoading = false
            )
        }
    }

    private fun onRequestError(
        message: String?
    ) {
        searchMovieResultsMutable.update {
            MovieSearchState.Error(message ?: "Unexpected Error")
        }
    }

    private fun onRequestLoading() {
        val movieSearchState = searchMovieResultsMutable.value

        if (movieSearchState is MovieSearchState.Initial || movieSearchState is MovieSearchState.NoResults) {
            searchMovieResultsMutable.update { MovieSearchState.Loading }
        }

        if (movieSearchState is MovieSearchState.Result) {
            if (movieSearchState.movies.isEmpty()) {
                searchMovieResultsMutable.update { MovieSearchState.Loading }
            }

            if (movieSearchState.movies.isNotEmpty()) {
                searchMovieResultsMutable.update { MovieSearchState.Loading }
            }
        }
    }
}
