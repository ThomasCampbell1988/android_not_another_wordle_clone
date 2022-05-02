package self.tomc.nawc

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import self.tomc.nawc.game.GuessState
import self.tomc.nawc.game.Guesses
import self.tomc.nawc.game.TileMatchState

@Composable
fun WordleGrid(guesses: Guesses, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            WordleRow(guesses.firstGuess)
            WordleRow(guesses.secondGuess)
            WordleRow(guesses.thirdGuess)
            WordleRow(guesses.fourthGuess)
            WordleRow(guesses.fifthGuess)
            WordleRow(guesses.sixthGuess)
        }
    }
}

@Composable
fun WordleRow(guess: GuessState, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Tile(guess.firstTile.letter, modifier = Modifier.weight(1f), tileMatchStateColors(guess.firstTile.tileMatchState))
        Tile(guess.secondTile.letter, modifier = Modifier.weight(1f), tileMatchStateColors(guess.secondTile.tileMatchState))
        Tile(guess.thirdTile.letter, modifier = Modifier.weight(1f), tileMatchStateColors(guess.thirdTile.tileMatchState))
        Tile(guess.fourthTile.letter, modifier = Modifier.weight(1f), tileMatchStateColors(guess.fourthTile.tileMatchState))
        Tile(guess.fifthTile.letter, modifier = Modifier.weight(1f), tileMatchStateColors(guess.fifthTile.tileMatchState))
    }
}

@Composable
fun Tile(
    letter: Char?,
    modifier: Modifier = Modifier,
    backgroundAndContentColors: BackgroundAndContentColors = BackgroundAndContentColors(backgroundColor = Color.White, contentColor = Color.Black)
) {
    Surface(
        color = backgroundAndContentColors.backgroundColor,
        border = BorderStroke(2.dp, Color.Gray),
        modifier = modifier.then(Modifier.padding(2.dp, 2.dp))
    ){
        Text(
            text = letter?.toString() ?: "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 18.dp),
            textAlign = TextAlign.Center,
            fontSize = 6.em,
            color = backgroundAndContentColors.contentColor
        )
    }
}

@Composable
fun tileMatchStateColors(tileMatchState: TileMatchState): BackgroundAndContentColors {
    return when(tileMatchState) {
        TileMatchState.UNKNOWN -> BackgroundAndContentColors(backgroundColor = MaterialTheme.colors.activeTileGrey, contentColor = MaterialTheme.colors.keyboardText)
        TileMatchState.MATCHED -> BackgroundAndContentColors(backgroundColor = MaterialTheme.colors.matchedGreen, contentColor = MaterialTheme.colors.keyboardText)
        TileMatchState.PRESENT -> BackgroundAndContentColors(backgroundColor = MaterialTheme.colors.presentYellow, contentColor = MaterialTheme.colors.keyboardText)
        TileMatchState.ABSENT -> BackgroundAndContentColors(backgroundColor = MaterialTheme.colors.mismatchGrey, contentColor = MaterialTheme.colors.keyboardText)
    }
}