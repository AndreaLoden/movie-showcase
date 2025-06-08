import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.presentation.movies.MoviesViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoviesGridScreen(
    moviesViewModel: MoviesViewModel = koinViewModel<MoviesViewModel>()
) {
    val state by moviesViewModel.state.collectAsState()
    val paginationState by moviesViewModel.paginationState.collectAsState()
    val isRefresh by moviesViewModel.isRefresh.collectAsState()

    val movies = state.movies

    Box(modifier = Modifier.fillMaxSize()) {
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
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(movies, key = { _, movie -> movie.id }) { index, movie ->

                MovieGridItemWithoutImage(movie)

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
fun MovieGridItemWithoutImage(
    movie: Movie
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Placeholder box representing the image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Image",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp),
                maxLines = 2
            )
        }
    }
}
