import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.andrea.imdbshowcase.App
import kotlin.test.Test

class ExampleTest {

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myTest() = runComposeUiTest {
        // Declares a mock UI to demonstrate API calls
        //
        // Replace with your own declarations to test the code of your project
        setContent {
            App()  // your composable under test
        }

        // Tests the declared UI with assertions and actions of the Compose Multiplatform testing API
        onNodeWithText("Hello, Compose Multiplatform!").assertIsDisplayed()
    }
}