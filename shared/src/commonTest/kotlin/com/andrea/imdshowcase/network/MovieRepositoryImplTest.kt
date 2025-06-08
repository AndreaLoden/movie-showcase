package com.andrea.imdshowcase.network

import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.core.repository.Resource
import com.andrea.imdbshowcase.network.MovieRepositoryImpl
import com.andrea.imdbshowcase.network.TheMovieDataBaseApi
import com.andrea.imdbshowcase.network.model.MovieDto
import com.andrea.imdbshowcase.network.model.MovieResultsDto
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MovieRepositoryImplTest {

    private val testMovieDto = MovieDto(id = 1, title = "Test", posterPath = "")
    private val testMovie = Movie(id = "1", title = "Test", imgURL = "")

    @Test
    fun `getMoviesRemote emits loading and success when API returns movies`() = runTest {
        // Arrange
        val fakeApi = object : TheMovieDataBaseApi {
            override suspend fun getMovies(page: Int): MovieResultsDto {
                return MovieResultsDto(movies = listOf(testMovieDto))
            }
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
        val fakeApi = object : TheMovieDataBaseApi {
            override suspend fun getMovies(page: Int): MovieResultsDto {
                throw RuntimeException("Network Error")
            }
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
