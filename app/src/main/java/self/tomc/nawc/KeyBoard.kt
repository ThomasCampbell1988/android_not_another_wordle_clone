package self.tomc.nawc

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import self.tomc.nawc.game.KeyBoardState

@Composable
fun KeyBoard(
    keyBoardState: KeyBoardState,
    modifier: Modifier = Modifier,
    onLetterClick: (letter: Char) -> Unit = {},
    onSubmitClicked: () -> Unit = {},
    onDeleteClicked: () -> Unit = {}
) {
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
                        onClick = { onLetterClick(letter.single()) },
                        backgroundAndContentColors = keyboardColorForLetter(letter.single(), keyBoardState)
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
                        onClick = { onLetterClick(letter.single()) },
                        backgroundAndContentColors = keyboardColorForLetter(letter.single(), keyBoardState)
                    )
                }
                Spacer(modifier = Modifier.weight(0.5f))
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Key(
                    "ENTER",
                    modifier = Modifier.weight(1.5f),
                    onClick = { onSubmitClicked() }
                )
                for (letter in bottomRow) {
                    Key(
                        letter,
                        modifier = Modifier.weight(1f),
                        onClick = { onLetterClick(letter.single()) },
                        backgroundAndContentColors = keyboardColorForLetter(letter.single(), keyBoardState)
                    )
                }
                Key(
                    "DEL",
                    modifier = Modifier.weight(1.5f),
                    onClick = { onDeleteClicked() }
                )
            }
        }
    }
}

@Composable
fun keyboardColorForLetter(letter: Char, keyBoardState: KeyBoardState): BackgroundAndContentColors {
    return when(letter) {
        in keyBoardState.matchedLetters -> BackgroundAndContentColors(MaterialTheme.colors.matchedGreen, MaterialTheme.colors.keyboardText)
        in keyBoardState.presentLetters -> BackgroundAndContentColors(MaterialTheme.colors.presentYellow, MaterialTheme.colors.keyboardText)
        in keyBoardState.absentLetters -> BackgroundAndContentColors(MaterialTheme.colors.mismatchGrey, MaterialTheme.colors.keyboardText)
        else -> BackgroundAndContentColors(MaterialTheme.colors.keyboardBackground, MaterialTheme.colors.keyboardText)
    }
}

@Composable
fun Key(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit = {},
    backgroundAndContentColors: BackgroundAndContentColors = BackgroundAndContentColors(MaterialTheme.colors.keyboardBackground, MaterialTheme.colors.keyboardText)
) {
    Button(
        onClick = { onClick(text) },
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundAndContentColors.backgroundColor,
            contentColor = backgroundAndContentColors.contentColor
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