package com.andrea.imdshowcase.network

import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.network.MovieRepositoryImpl
import com.andrea.imdbshowcase.network.TheMovieDataBaseApi
import com.andrea.imdbshowcase.network.model.MovieDto
import com.andrea.imdbshowcase.network.model.MovieResultsDto
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MovieRepositoryImplTest {

    private val testMovieDto = MovieDto(
        adult = false,
        backdropPath = "adsaddsa",
        genreIds = listOf(),
        id = 1,
        originalLanguage = "adsaddsa",
        originalTitle = "adsaddsa",
        overview = "adsaddsa",
        popularity = 5.0,
        posterPath = "adsaddsa",
        releaseDate = "adsaddsa",
        title = "Movie Title",
        video = false,
        voteAverage = 3.4,
        voteCount = 50,
        mediaType = "adsaddsa"
    )

    private val testMovie = Movie(
        id = "1",
        title = "Movie Title",
        imgURL = "https://image.tmdb.org/t/p/w500/adsaddsa",
        backdrop_path = "adsaddsa",
        tagline = "",
        overview = "adsaddsa",
        genres = listOf(),
        runtime = -1,
        spoken_languages = listOf(),
        vote_average = 3.4,
        release_date = "adsaddsa"
    )

    @Test
    fun `getMoviesRemote emits loading and success when API returns movies`() = runTest {
        // Arrange
        val fakeApi = mock<TheMovieDataBaseApi> {
            everySuspend { getMovies(any<Int>()) } returns MovieResultsDto(movies = listOf(testMovieDto))
        }

        val repository = MovieRepositoryImpl(fakeApi)

        // Act
        val results = repository.getMoviesRemote(skip = 1).toList()

        // Assert
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Success)
        assertEquals(listOf(testMovie), (results[1] as Resource.Success).data)
    }

    @Test
    fun `getMoviesRemote emits loading and error when API throws exception`() = runTest {
        // Arrange

        val fakeApi = mock<TheMovieDataBaseApi> {
            everySuspend { getMovies(any<Int>()) } throws RuntimeException("Network Error")
        }

        val repository = MovieRepositoryImpl(fakeApi)

        // Act
        val results = repository.getMoviesRemote(skip = 1).toList()

        // Assert
        assertEquals(2, results.size)
        assertTrue(results[0] is Resource.Loading)
        assertTrue(results[1] is Resource.Error)
        assertEquals("Network Error", (results[1] as Resource.Error).message)
    }
}
