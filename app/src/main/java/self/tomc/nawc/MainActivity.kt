package self.tomc.nawc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import self.tomc.nawc.data.ApplicationDictionaryProvider
import self.tomc.nawc.data.DictionaryProvider
import self.tomc.nawc.game.Guesses
import self.tomc.nawc.game.WordleGame
import self.tomc.nawc.game.WordleGameState
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

data class WordleUiState(
    val letter: String? = "A"
)

class WordleViewModel(dictionaryProvider: DictionaryProvider): ViewModel() {

    private val game: WordleGame = WordleGame("Apple", dictionaryProvider.getWordDictionary())

    private val _gameState = MutableStateFlow(game.state)
    val gameState: StateFlow<WordleGameState>
        get() = _gameState

    fun inputLetter(letter: String) {
        game.inputLetter(letter.single())
        _gameState.value = game.state
    }

    fun delete() {
        game.deleteLetter()
        _gameState.value = game.state
    }

    fun enter() {
        game.submitGuess()
        _gameState.value = game.state
    }

    fun notInWordListShown() {

    }

    fun rowRevealed() {

    }


    class WordleViewModelFactory (
        private val dictionaryProvider: DictionaryProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            (WordleViewModel(dictionaryProvider) as T)
    }
}

@Composable
fun Wordle(viewModel: WordleViewModel, modifier: Modifier =  Modifier) {

    val gameState by viewModel.gameState.collectAsState()

    Surface(
        modifier = modifier.then(Modifier.fillMaxSize()),
        color = MaterialTheme.colors.background
    ) {
        Column {
            Title(stringResource(R.string.title), modifier = Modifier.fillMaxWidth())
            Divider()
            WordleGrid(gameState.guesses, modifier = Modifier.weight(1f))
            KeyBoard(viewModel)
        }
    }
}

@Composable
fun Title(text: String, modifier: Modifier =  Modifier) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        modifier = modifier.then(Modifier.padding(16.dp)),
        fontSize = 20.sp
    )
}

@Composable
fun WordleGrid(guesses: Guesses, modifier: Modifier =  Modifier) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            WordleRow(guesses.firstGuess.asOptionalCharList())
            WordleRow(guesses.secondGuess.asOptionalCharList())
            WordleRow(guesses.thirdGuess.asOptionalCharList())
            WordleRow(guesses.fourthGuess.asOptionalCharList())
            WordleRow(guesses.fifthGuess.asOptionalCharList())
            WordleRow(guesses.sixthGuess.asOptionalCharList())
        }
    }
}

@Composable
fun WordleRow(letters: List<Char?>, modifier: Modifier =  Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        for(letter in letters) {
            Tile(letter, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun Tile(letter: Char?, modifier: Modifier =  Modifier) {
    Surface(
        color = MaterialTheme.colors.background,
        border = BorderStroke(2.dp, Color.Gray),
        modifier = modifier.then(Modifier.padding(2.dp, 2.dp))
    ){
        Text(
            text = letter?.toString() ?: "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 18.dp),
            textAlign = TextAlign.Center,
            fontSize = 6.em
        )
    }
}

@Composable
fun KeyBoard(viewModel: WordleViewModel, modifier: Modifier =  Modifier) {
    val topRow = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
    val middleRow = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
    val bottomRow = listOf("Z", "X", "C", "V", "B", "N", "M")
    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (letter in topRow) {
                    Key(
                        letter,
                        Modifier.weight(1f),
                        onClick = { viewModel.inputLetter(letter) }
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(0.5f))
                for (letter in middleRow) {
                    Key(
                        letter,
                        Modifier.weight(1f),
                        onClick = { viewModel.inputLetter(letter) }
                    )
                }
                Spacer(modifier = Modifier.weight(0.5f))
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Key("ENTER", modifier = Modifier.weight(1.5f))
                for (letter in bottomRow) {
                    Key(
                        letter,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.inputLetter(letter) }
                    )
                }
                Key("DEL", modifier = Modifier.weight(1.5f))
            }
        }
    }
}

@Composable
fun Key(text: String, modifier: Modifier = Modifier, onClick: (String) -> Unit = {}) {
    Button(
        onClick = { onClick(text) },
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.LightGray,
            contentColor = Color.Black
        ),
        modifier = modifier.then(
            Modifier
                .padding(2.dp, 2.dp)
                .clip(RoundedCornerShape(6.dp))
        )
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 24.dp),
            textAlign = TextAlign.Center,
        )
    }
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
                override fun getWordDictionary() = listOf("APPLE", "PEARS", "MANGO", "PAPPY", "HAPPY", "ZIPPY", "GHOST")
            }))
        }
    }
}