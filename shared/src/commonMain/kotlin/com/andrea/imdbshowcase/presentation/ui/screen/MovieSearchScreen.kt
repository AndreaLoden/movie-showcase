import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.andrea.imdbshowcase.core.model.Movie
import com.andrea.imdbshowcase.presentation.state.MovieSearchState
import com.andrea.imdbshowcase.presentation.viewmodel.MovieSearchViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesSearchScreen(
    navHostController: NavHostController,
    movieSearchViewModel: MovieSearchViewModel = koinViewModel()
) {
    val state by movieSearchViewModel.searchMovieResults.collectAsState()
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
                movieSearchViewModel.onNewSearchQuery(text)
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
                is MovieSearchState.Error ->
                    ErrorState(safeState.message) { movieSearchViewModel.onNewSearchQuery(text) }

                MovieSearchState.Initial -> InitialState()

                MovieSearchState.Loading -> LoadingState()

                MovieSearchState.NoResults -> NoResultsState()

                is MovieSearchState.Result -> ResultsState(
                    state = safeState,
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
            style = MaterialTheme.typography.titleMedium,
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
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ResultsState(
    state: MovieSearchState.Result,
    navHostController: NavHostController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Top results",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(
            items = state.movies,
            key = { movie -> movie.id }
        ) { movie ->
            MovieListItem(navHostController, movie, Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun MovieListItem(
    navHostController: NavHostController,
    movie: Movie,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { navHostController.navigate("detail/${movie.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = movie.imgURL,
                contentScale = ContentScale.Crop,
                contentDescription = "Movie poster of ${movie.title}"
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
