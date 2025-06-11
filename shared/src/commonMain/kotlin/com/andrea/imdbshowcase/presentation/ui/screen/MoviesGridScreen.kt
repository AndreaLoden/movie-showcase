package com.andrea.imdbshowcase.presentation.ui.screen

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
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.presentation.viewmodel.MoviesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoviesGridScreen(
    navHostController: NavHostController,
    moviesViewModel: MoviesViewModel = koinViewModel<MoviesViewModel>()
) {
    val state by moviesViewModel.moviesState.collectAsState()
    val paginationState by moviesViewModel.paginationState.collectAsState()
    val isRefresh by moviesViewModel.isRefreshState.collectAsState()

    val movies = state.movies

    Box(
        modifier = Modifier.background(Color.White)
            .fillMaxSize()
    ) {
        if (state.error.isNotEmpty()) {
            // Show error message with retry button
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { moviesViewModel.getMoviesPaginated() }) {
                    Text(text = "Retry")
                }
            }
            return@Box
        }

        if (state.isLoading && movies.isEmpty()) {
            // Show initial loading indicator
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@Box
        }
        // Movie grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 8.dp,
                top = 8.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
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
                        text = "Movie releases up to ${state.todaysDate}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            item(span = { GridItemSpan(1) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.6f)
                        .clickable { navHostController.navigate("search") },
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

            itemsIndexed(movies, key = { _, movie -> movie.id }) { index, movie ->

                MovieGridItem(navHostController, movie)

                // Trigger pagination when user scrolls near the end
                if (index >= movies.lastIndex - 3 && !paginationState.isLoading && !paginationState.endReached) {
                    moviesViewModel.getMoviesPaginated()
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

        if (isRefresh) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.TopCenter)
            ) {
                Text(
                    text = "Refreshing...",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
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
            .clickable { navController.navigate("detail/${movie.id}") },
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
