package com.andrea.tmdbshowcase.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.andrea.tmdbshowcase.core.model.Movie
import com.andrea.tmdbshowcase.presentation.state.MovieGridPaginationState
import com.andrea.tmdbshowcase.presentation.state.MovieGridUiState
import com.andrea.tmdbshowcase.presentation.util.Orientation
import com.andrea.tmdbshowcase.presentation.util.orientationState
import com.andrea.tmdbshowcase.presentation.viewmodel.MovieGridViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoviesGridScreen(
    navHostController: NavHostController,
    movieGridViewModel: MovieGridViewModel = koinViewModel<MovieGridViewModel>()
) {
    val state by movieGridViewModel.moviesState.collectAsState()
    val paginationState by movieGridViewModel.paginationState.collectAsState()

    Box(modifier = Modifier.background(Color.White).fillMaxSize()) {
        when (val safeState = state) {
            is MovieGridUiState.Loading ->
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            is MovieGridUiState.Error -> ErrorState(safeState, movieGridViewModel)

            is MovieGridUiState.Success -> MovieGrid(
                safeState,
                navHostController,
                paginationState,
                movieGridViewModel
            )
        }
    }
}

@Composable
fun MovieGrid(
    safeState: MovieGridUiState.Success,
    navHostController: NavHostController,
    paginationState: MovieGridPaginationState,
    movieGridViewModel: MovieGridViewModel
) {
    val orientation by orientationState()

    val columns = if (orientation == Orientation.Portrait) {
        2
    } else {
        5
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp,
            top = 8.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues()
                .calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Text(
                    text = "Movie releases up to ${safeState.todaysDate}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        item(span = { GridItemSpan(1) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.6f)
                    .clickable { navHostController.navigate("movie-search") },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Navigate to Search",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(48.dp) // Adjust size as needed
                    )
                }
            }
        }

        itemsIndexed(safeState.movies, key = { _, movie -> movie.id }) { index, movie ->

            MovieGridItem(navHostController, movie)

            // Trigger pagination when user scrolls near the end
            if (index >= safeState.movies.lastIndex - 3 && !paginationState.isLoading && !paginationState.endReached) {
                movieGridViewModel.getMoviesPaginated()
            }
        }

        // Show pagination loading indicator as footer
        if (paginationState.isLoading) {
            item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ErrorState(
    safeState: MovieGridUiState.Error,
    movieGridViewModel: MovieGridViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = safeState.message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { movieGridViewModel.getMoviesPaginated() }) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun MovieGridItem(
    navController: NavHostController,
    movie: Movie
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.6f)
            .clickable { navController.navigate("movie-details/${movie.id}") },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.DarkGray),
                model = movie.imgURL,
                contentScale = ContentScale.Crop,
                contentDescription = "Movie poster of ${movie.title}"
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
