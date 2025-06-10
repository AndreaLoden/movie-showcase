package com.andrea.imdbshowcase.presentation.viewmodel

import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.MovieRepository
import com.andrea.imdbshowcase.core.repository.Resource
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: MovieRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = mock()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createMovie(id: String, title: String, imgURL: String = "adsaddsa") = Movie(
        id = id,
        imgURL = imgURL,
        title = title,
        tagline = "adsaddsa",
        overview = "adsaddsa",
        genres = listOf(),
        runtime = 120,
        spokenLanguages = listOf(),
        voteAverage = 3.4,
        releaseDate = "adsaddsa"
    )

    @Test
    fun `init loads movies`() = runTest {
        val movie = createMovie("1234565765432", "Test Movie")
        every { repo.getMovieDetailsRemote("1234565765432") } returns flowOf(
            Resource.Success(movie)
        )

        val viewModel = MovieDetailViewModel(repo, this)
        viewModel.updateUiState("1234565765432")

        advanceUntilIdle()

        assertEquals(movie, viewModel.movieDetailsState.value.movie)
        assertFalse(viewModel.movieDetailsState.value.isLoading)
        assertEquals("", viewModel.movieDetailsState.value.error)
    }

    @Test
    fun `onRequestError updates error state`() = runTest {
        every { repo.getMovieDetailsRemote(any<String>()) } returns flowOf(Resource.Error("Network failure"))

        val viewModel = MovieDetailViewModel(repo, this)
        viewModel.updateUiState("1234565765432")

        advanceUntilIdle()

        assertEquals("Network failure", viewModel.movieDetailsState.value.error)
        assertFalse(viewModel.movieDetailsState.value.isLoading)
    }

    @Test
    fun `onRequestLoading sets loading state correctly`() = runTest {
        every { repo.getMovieDetailsRemote(any<String>()) } returns flowOf(Resource.Loading())

        val viewModel = MovieDetailViewModel(repo, this)
        viewModel.updateUiState("1234565765432")

        advanceUntilIdle()

        assertTrue(viewModel.movieDetailsState.value.isLoading)
    }
}
