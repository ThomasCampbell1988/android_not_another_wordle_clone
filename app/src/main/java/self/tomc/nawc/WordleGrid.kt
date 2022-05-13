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
import self.tomc.nawc.game.LetterTileState
import self.tomc.nawc.game.TileMatchState

@Composable
fun WordleGrid(guesses: Guesses, modifier: Modifier = Modifier, revealing: GuessState? = null, onRevealedRow: () -> Unit = {}) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            guesses.asList().forEach { guess ->
                val revealingForRow = if(revealing != null && guess == guesses.activeGuess()) revealing else null
                WordleRow(guess, revealing = revealingForRow, onRevealedRow = onRevealedRow)
            }
        }
    }
}

@Composable
fun WordleRow(guess: GuessState, modifier: Modifier = Modifier, revealing: GuessState? = null, onRevealedRow: () -> Unit = {}) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.then(Modifier.fillMaxWidth())
    ) {
        Tile(guess.firstTile, modifier = Modifier.weight(1f), revealing = revealing?.firstTile, animationDelayMillis = 200)
        Tile(guess.secondTile, modifier = Modifier.weight(1f), revealing = revealing?.secondTile, animationDelayMillis = 400)
        Tile(guess.thirdTile, modifier = Modifier.weight(1f), revealing = revealing?.thirdTile, animationDelayMillis = 600)
        Tile(guess.fourthTile, modifier = Modifier.weight(1f), revealing = revealing?.fourthTile, animationDelayMillis = 800)
        Tile(guess.fifthTile, modifier = Modifier.weight(1f), revealing = revealing?.fifthTile, animationDelayMillis = 1000, onRevealedLetter = onRevealedRow)
    }
}

@Composable
fun Tile(
    letter: LetterTileState,
    modifier: Modifier = Modifier,
    revealing: LetterTileState? = null,
    onRevealedLetter: () -> Unit = {},
    animationDelayMillis: Int = 0
) {
    val flipAnimationTime = 600

    var rotated by remember { mutableStateOf(false) }
    var showRevealing by remember { mutableStateOf(false) }
    rotated = revealing != null

    val rotation by animateFloatAsState(
        targetValue = if (rotated) 90f else 0f,
        animationSpec = repeatable(
            2,
            tween(
                flipAnimationTime,
                easing = LinearEasing,
                delayMillis = animationDelayMillis
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(revealing) {
        if(revealing != null) {
            delay(animationDelayMillis.toLong())
            delay(flipAnimationTime.toLong())
            showRevealing = true
            delay(flipAnimationTime.toLong())
            onRevealedLetter()
        }
    }

    val rowModifier = if(revealing != null) Modifier.graphicsLayer {
        rotationX = rotation
    } else Modifier

    val letterToShow = if(revealing != null && showRevealing) revealing else letter
    val backgroundAndContentColors = tileMatchStateColors(letterToShow.tileMatchState)

    Surface(
        color = backgroundAndContentColors.backgroundColor,
        border = BorderStroke(2.dp, Color.Gray),
        modifier = modifier.then(rowModifier).then(Modifier.padding(2.dp, 2.dp))
    ){
        Text(
            text = letterToShow.letter?.toString() ?: "",
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