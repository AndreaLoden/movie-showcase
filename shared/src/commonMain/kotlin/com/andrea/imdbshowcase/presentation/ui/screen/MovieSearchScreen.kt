import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.andrea.imdbshowcase.presentation.state.MovieSearchState
import com.andrea.imdbshowcase.presentation.state.PaginationState
import com.andrea.imdbshowcase.presentation.ui.screen.MovieGridItemWithoutImage
import com.andrea.imdbshowcase.presentation.viewmodel.MovieSearchViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesSearchScreen(
    navHostController: NavHostController,
    movieSearchViewModel: MovieSearchViewModel = koinViewModel()
) {
    val state by movieSearchViewModel.searchMovieResults.collectAsState()
    val paginationState by movieSearchViewModel.paginationState.collectAsState()
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.LightGray),
            title = { Text("Search", maxLines = 1, overflow = TextOverflow.Ellipsis) },
            navigationIcon = {
                IconButton(onClick = { navHostController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        )

        TextField(
            placeholder = { Text("Search") },
            minLines = 1,
            maxLines = 1,
            value = text,
            onValueChange = {
                text = it
                movieSearchViewModel.userInput.value = text
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            when (val safeState = state) {
                is MovieSearchState.Error -> ErrorState(safeState.message) {
                    movieSearchViewModel.getMoviesPaginated(text)
                }

                MovieSearchState.Initial -> InitialState()

                MovieSearchState.Loading -> LoadingState()

                MovieSearchState.NoResults -> NoResultsState()

                is MovieSearchState.Refresh -> RefreshingState()

                is MovieSearchState.Result -> ResultsState(
                    state = safeState,
                    paginationState = paginationState,
                    onPaginate = { movieSearchViewModel.getMoviesPaginated(text) },
                    navHostController = navHostController
                )
            }
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun InitialState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = "Start typing to see results",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun NoResultsState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = "No results :(",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RefreshingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = "Refreshing...",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ResultsState(
    state: MovieSearchState.Result,
    paginationState: PaginationState,
    onPaginate: () -> Unit,
    navHostController: NavHostController
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(state.movies, key = { _, movie -> movie.id }) { index, movie ->
            MovieGridItemWithoutImage(navHostController, movie)

            if (index >= state.movies.lastIndex - 3 && !paginationState.isLoading && !paginationState.endReached) {
                onPaginate()
            }
        }

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
