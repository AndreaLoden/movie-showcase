package com.andrea.imdbshowcase.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.presentation.state.MovieSearchState
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

    fun onNewSearchQuery(query: String) {
        userInput.update { query }
    }

    private fun handleQuery(query: String) {
        if (query.isBlank()) {
            searchMovieResultsMutable.update { MovieSearchState.Initial }
        } else {
            getMovies(query)
        }
    }

    private fun getMovies(query: String) {
        scope.launch {
            movieRepository.getMoviesForQueryRemote(query)
                .distinctUntilChanged()
                .collectLatest { result ->
                    when (result) {
                        is Resource.Success -> result.data?.let { data ->
                            onRequestSuccess(data)
                        }

                        is Resource.Error -> onRequestError(result.message)
                        is Resource.Loading -> onRequestLoading()
                    }
                }
        }
    }

    private fun onRequestSuccess(data: List<Movie>) {
        searchMovieResultsMutable.value =
            if (data.isEmpty()) {
                MovieSearchState.NoResults
            } else {
                MovieSearchState.Result(data)
            }
    }

    private fun onRequestError(message: String?) {
        searchMovieResultsMutable.update {
            MovieSearchState.Error(message ?: "Unexpected Error")
        }
    }

    private fun onRequestLoading() {
        searchMovieResultsMutable.update { MovieSearchState.Loading }
    }
}
