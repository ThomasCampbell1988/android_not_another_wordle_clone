package self.tomc.nawc

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import self.tomc.nawc.data.ApplicationDictionaryProvider
import self.tomc.nawc.data.DictionaryProvider
import self.tomc.nawc.ui.theme.NAWCTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WordleViewModel by viewModels {
        WordleViewModel.WordleViewModelFactory(ApplicationDictionaryProvider(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp(viewModel)
        }
    }
}

@Composable
fun MyApp(viewModel: WordleViewModel) {
    NAWCTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Wordle(viewModel)
        }
    }
}

@Composable
fun Wordle(viewModel: WordleViewModel, modifier: Modifier = Modifier) {

    val gameState by viewModel.gameState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    if(uiState.showInvalidWordMessage) {
        Toast.makeText(LocalContext.current, "Word not in dictionary", Toast.LENGTH_SHORT).show()
        viewModel.haveShownInvalidWordMessage()
    }

    Surface(
        modifier = modifier.then(Modifier.fillMaxSize()),
        color = MaterialTheme.colors.primarySurface
    ) {
        Column {
            Title(stringResource(R.string.title), modifier = Modifier.fillMaxWidth())
            Divider()
            WordleGrid(gameState.guesses, modifier = Modifier.weight(1f), uiState.revealRow, onRevealedRow = {viewModel.haveRevealedRow()})
            KeyBoard(
                gameState.keyBoardState,
                onLetterClick = { char: Char -> viewModel.inputLetter(char)},
                onSubmitClicked = { viewModel.enter() },
                onDeleteClicked = { viewModel.delete() }
            )
        }
    }
}

@Composable
fun Title(text: String, modifier: Modifier =  Modifier) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        modifier = modifier.then(Modifier.padding(16.dp)),
        fontSize = 20.sp,
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NAWCTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Wordle(viewModel = WordleViewModel(dictionaryProvider = object: DictionaryProvider{
                override fun getWordDictionary() = listOf("APPLE", "PEARS", "MANGO", "POPPY", "HAPPY", "ZIPPY", "GHOST")
            }))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DarkThemePreview() {
    NAWCTheme(darkTheme = true) {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Wordle(viewModel = WordleViewModel(dictionaryProvider = object: DictionaryProvider{
                override fun getWordDictionary() = listOf("APPLE", "PEARS", "MANGO", "POPPY", "HAPPY", "ZIPPY", "GHOST")
            }))
        }
    }
}