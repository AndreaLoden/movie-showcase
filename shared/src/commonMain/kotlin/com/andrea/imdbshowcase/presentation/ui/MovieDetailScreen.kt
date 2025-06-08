
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.andrea.imdbshowcase.presentation.viewmodel.MovieDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MoviesDetailScreen(
    movieId: String,
    navHostController: NavHostController,
    detailViewModel: MovieDetailViewModel = koinViewModel<MovieDetailViewModel>()
) {
    val movieDetailsState by detailViewModel.movieDetailsState.collectAsState()

    detailViewModel.updateUiState(movieId)

    Box(modifier = Modifier.fillMaxSize()) {
        if (movieDetailsState.error.isNotEmpty()) {
            // Show error message with retry button
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = movieDetailsState.error, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    detailViewModel.updateUiState(movieId)
                }) {
                    Text(text = "Retry")
                }
            }
            return@Box
        }

        if (movieDetailsState.isLoading && movieDetailsState.movie == null) {
            // Show initial loading indicator
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@Box
        }

        val movie = movieDetailsState.movie
        if (movie != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Poster Image
                movie.imgURL.let {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        model = movie.imgURL,
                        contentDescription = movie.title
                    )
                }

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

                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = movie.overview)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Genres: ${movie.genres.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Runtime: ${movie.runtime} min",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Languages: ${movie.spoken_languages.joinToString { it.english_name }}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Release Date: ${movie.release_date}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Rating: ${movie.vote_average}/10",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
