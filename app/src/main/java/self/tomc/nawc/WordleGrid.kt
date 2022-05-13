package self.tomc.nawc

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kotlinx.coroutines.delay
import self.tomc.nawc.game.GuessState
import self.tomc.nawc.game.Guesses
import self.tomc.nawc.game.TileMatchState

@Composable
fun WordleGrid(guesses: Guesses, modifier: Modifier = Modifier, revealing: GuessState? = null, onRevealedRow: () -> Unit = {}) {
    val flipAnimationTime = 800

    var rotated by remember { mutableStateOf(false) }
    var showRevealing by remember { mutableStateOf(false) }
    rotated = revealing != null
    showRevealing = false

    val rotation by animateFloatAsState(
        targetValue = if (rotated) 90f else 0f,
        animationSpec = repeatable(
            2,
            tween(flipAnimationTime, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(revealing) {
        if(revealing != null) {
            delay(flipAnimationTime.toLong())
            showRevealing = true
            delay(flipAnimationTime.toLong())
            onRevealedRow()
            showRevealing = false
        }
    }

    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            guesses.asList().forEach { guess ->
                val rowModifier = if(revealing != null && guess == guesses.activeGuess()) Modifier.graphicsLayer {
                    rotationX = rotation
                } else Modifier
                val guessToShow = if(revealing != null && guess == guesses.activeGuess() && showRevealing) revealing else guess
                WordleRow(guessToShow, modifier = rowModifier)
            }
        }
    }
}

@Composable
fun WordleRow(guess: GuessState, modifier: Modifier = Modifier, revealing: GuessState? = null) {
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