package com.andrea.tmdbshowcase.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.andrea.tmdbshowcase.presentation.state.MovieDetailState
import com.andrea.tmdbshowcase.presentation.viewmodel.MovieDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoviesDetailScreen(
    movieId: String,
    navHostController: NavHostController,
    detailViewModel: MovieDetailsViewModel = koinViewModel<MovieDetailsViewModel>()
) {
    val movieDetailsState by detailViewModel.movieDetailsState.collectAsState()

    detailViewModel.loadMovieWithId(movieId)

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        when (val safeMovieDetailsState = movieDetailsState) {
            is MovieDetailState.Error -> ErrorState(safeMovieDetailsState, detailViewModel, movieId)

            MovieDetailState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            is MovieDetailState.Success -> MovieDetails(safeMovieDetailsState, navHostController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MovieDetails(
    safeMovieDetailsState: MovieDetailState.Success,
    navHostController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val movie = safeMovieDetailsState.movie
        TopAppBar(
            colors = TopAppBarDefaults
                .topAppBarColors()
                .copy(containerColor = Color.LightGray),
            title = {
                Text(movie.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            },
            navigationIcon = {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            // Poster Image
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                model = movie.imgURL,
                contentDescription = movie.title
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium
            )

            if (movie.tagline.isNotBlank()) {
                Text(
                    text = "\"${movie.tagline}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (movie.overview.isNotBlank()) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = movie.overview)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (movie.genres.isNotEmpty()) {
                Text(
                    text = "Genres: ${movie.genres.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (movie.runtime >= 0) {
                Text(
                    text = "Runtime: ${movie.runtime} min",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (movie.spokenLanguages.isNotEmpty()) {
                Text(
                    text = "Languages: ${movie.spokenLanguages.joinToString { it.englishName }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (movie.releaseDate.isNotBlank()) {
                Text(
                    text = "Release Date: ${movie.releaseDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (movie.voteAverage >= 0.0) {
                Text(
                    text = "Rating: ${movie.voteAverage}/10.0",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ErrorState(
    safeMovieDetailsState: MovieDetailState.Error,
    detailViewModel: MovieDetailsViewModel,
    movieId: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = safeMovieDetailsState.message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            detailViewModel.loadMovieWithId(movieId)
        }) {
            Text(text = "Retry")
        }
    }
}
