package com.andrea.tmdbshowcase.presentation.viewmodel

import com.andrea.tmdbshowcase.core.model.Movie
import com.andrea.tmdbshowcase.core.repository.MovieRepository
import com.andrea.tmdbshowcase.core.repository.Resource
import com.andrea.tmdbshowcase.presentation.state.MovieDetailState
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {

    private val testMovie = Movie(
        id = "123",
        imgURL = "https://image.tmdb.org/t/p/w500/test.jpg",
        title = "Test Movie",
        tagline = "",
        overview = "A test movie",
        genres = emptyList(),
        runtime = -1,
        spokenLanguages = emptyList(),
        voteAverage = 4.5,
        releaseDate = "2024-01-01"
    )

    @Test
    fun `loadMovieWithId sets state to Success when repository returns data`() = runTest {
        val fakeRepo = mock<MovieRepository> {
            everySuspend { getMovieDetailsRemote("123") } returns flow {
                emit(Resource.Loading())
                emit(Resource.Success(testMovie))
            }
        }

        val viewModel = MovieDetailsViewModel(fakeRepo, this)

        viewModel.loadMovieWithId("123")
        advanceUntilIdle()

        val state = viewModel.movieDetailsState.value
        assertEquals(MovieDetailState.Success(testMovie), state)
    }

    @Test
    fun `loadMovieWithId sets state to Error when repository returns error`() = runTest {
        val fakeRepo = mock<MovieRepository> {
            everySuspend { getMovieDetailsRemote("123") } returns flow {
                emit(Resource.Loading())
                emit(Resource.Error("Network failed"))
            }
        }

        val viewModel = MovieDetailsViewModel(fakeRepo, this)

        viewModel.loadMovieWithId("123")
        advanceUntilIdle()

        val state = viewModel.movieDetailsState.value
        assertEquals(MovieDetailState.Error("Network failed"), state)
    }

    @Test
    fun `loadMovieWithId initially sets state to Loading`() = runTest {
        val fakeRepo = mock<MovieRepository> {
            everySuspend { getMovieDetailsRemote("123") } returns flow {
                emit(Resource.Loading())
            }
        }

        val viewModel = MovieDetailsViewModel(fakeRepo, this)

        viewModel.loadMovieWithId("123")
        advanceUntilIdle()

        val state = viewModel.movieDetailsState.value
        assertEquals(MovieDetailState.Loading, state)
    }
}
