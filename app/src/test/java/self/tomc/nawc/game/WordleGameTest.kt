package self.tomc.nawc.game

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import self.tomc.nawc.game.GuessResult.Submitted
import self.tomc.nawc.game.RunningState.*
import self.tomc.nawc.game.TileMatchState.*

class WordleGameTest {

    private val dictionary = listOf("APPLE", "PEARS", "MANGO", "PAPPY", "HAPPY", "ZIPPY", "GHOST")
    private lateinit var wordleGame: WordleGame

    @Before
    fun setUp() {
        wordleGame = WordleGame("APPLE", dictionary)
    }

    private fun guessWord(word: String): GuessResult {
        word.toCharArray().forEach { wordleGame.inputLetter(it) }
        return wordleGame.submitGuess()
    }

    @Test
    fun `new game starts in progress with empty guesses, and keyboard state`() {
        assertThat(wordleGame.state.runningState).isEqualTo(RunningState.IN_PROGRESS)
        assertThat(wordleGame.state.guesses).isEqualTo(Guesses())
        assertThat(wordleGame.state.keyBoardState).isEqualTo(KeyBoardState())
    }

    @Test
    fun `from new game, when input 6 letters, it updates first 5 tiles`() {
        wordleGame.inputLetter("A".single())
        wordleGame.inputLetter("B".single())
        wordleGame.inputLetter("C".single())
        wordleGame.inputLetter("D".single())
        wordleGame.inputLetter("E".single())
        wordleGame.inputLetter("F".single()) // row is filled so has no effect

        val firstGuess = wordleGame.state.guesses.firstGuess
        assertThat(firstGuess.firstTile.letter).isNotNull.isEqualTo("A".single())
        assertThat(firstGuess.secondTile.letter).isNotNull.isEqualTo("B".single())
        assertThat(firstGuess.thirdTile.letter).isNotNull.isEqualTo("C".single())
        assertThat(firstGuess.fourthTile.letter).isNotNull.isEqualTo("D".single())
        assertThat(firstGuess.fifthTile.letter).isNotNull.isEqualTo("E".single())

        assertThat(wordleGame.state.guesses.secondGuess.firstTile.letter).isNull()
    }

    @Test
    fun `deleting removes last letter in row if exists`() {
        wordleGame.deleteLetter() // row is empty so has no effect
        wordleGame.inputLetter("A".single())
        wordleGame.deleteLetter() // deletes A
        wordleGame.inputLetter("B".single())
        wordleGame.inputLetter("C".single())
        wordleGame.inputLetter("D".single())
        wordleGame.inputLetter("E".single())
        wordleGame.inputLetter("F".single())
        wordleGame.inputLetter("G".single()) // row is filled so has no effect
        wordleGame.deleteLetter() // deletes F
        wordleGame.inputLetter("H".single())

        val firstGuess = wordleGame.state.guesses.firstGuess
        assertThat(firstGuess.firstTile.letter).isNotNull.isEqualTo("B".single())
        assertThat(firstGuess.secondTile.letter).isNotNull.isEqualTo("C".single())
        assertThat(firstGuess.thirdTile.letter).isNotNull.isEqualTo("D".single())
        assertThat(firstGuess.fourthTile.letter).isNotNull.isEqualTo("E".single())
        assertThat(firstGuess.fifthTile.letter).isNotNull.isEqualTo("H".single())

        assertThat(wordleGame.state.guesses.secondGuess.firstTile.letter).isNull()
    }

    @Test
    fun `submitting incomplete guess returns incomplete guess result`() {
        wordleGame.inputLetter("A".single())
        wordleGame.inputLetter("B".single())
        wordleGame.inputLetter("C".single())
        wordleGame.inputLetter("D".single())
        wordleGame.inputLetter("E".single())
        wordleGame.deleteLetter()

        assertThat(wordleGame.submitGuess()).isEqualTo(GuessResult.IncompleteWord)
    }

    @Test
    fun `submitting word which is not in dictionary returns invalid guess result`() {
        val guessResult = guessWord("ABCDE")
        assertThat(guessResult).isEqualTo(GuessResult.InvalidWord)
    }

    @Test
    fun `submitting a winning word returns matched and revealed guess, adds letters to keyboard state and ends game`() {
        val guessResult = guessWord("APPLE")

        val expectedRevealedGuess = GuessState(
            firstTile = LetterTileState("A".single(), tileMatchState = MATCHED),
            secondTile = LetterTileState("P".single(), tileMatchState = MATCHED),
            thirdTile = LetterTileState("P".single(), tileMatchState = MATCHED),
            fourthTile = LetterTileState("L".single(), tileMatchState = MATCHED),
            fifthTile = LetterTileState("E".single(), tileMatchState = MATCHED),
            revealed = true
        )

        assertThat(guessResult).isEqualTo(
            Submitted(
                submitted = GuessState(
                    firstTile = LetterTileState("A".single()),
                    secondTile = LetterTileState("P".single()),
                    thirdTile = LetterTileState("P".single()),
                    fourthTile = LetterTileState("L".single()),
                    fifthTile = LetterTileState("E".single()),
                    revealed = false
                ),
                revealed = expectedRevealedGuess,
                runningState = WON
            )
        )

        assertThat(wordleGame.state.runningState).isEqualTo(WON)
        assertThat(wordleGame.state.guesses.firstGuess).isEqualTo(expectedRevealedGuess)

        val expectedKeyBoardState = KeyBoardState(matchedLetters = setOf("A".single(), "P".single(), "L".single(), "E".single()))
        assertThat(wordleGame.state.keyBoardState).isEqualTo(expectedKeyBoardState)
    }

    @Test
    fun `submitting an incorrect word returns revealed guess, and adds letters to keyboard state`() {
        // target word = "APPLE"
        val guessResult = guessWord("PAPPY")

        val expectedRevealedGuess = GuessState(
            firstTile = LetterTileState("P".single(), tileMatchState = PRESENT),
            secondTile = LetterTileState("A".single(), tileMatchState = PRESENT),
            thirdTile = LetterTileState("P".single(), tileMatchState = MATCHED),
            fourthTile = LetterTileState("P".single(), tileMatchState = ABSENT), // third P, but APPLE only has 2
            fifthTile = LetterTileState("Y".single(), tileMatchState = ABSENT),
            revealed = true
        )

        assertThat(guessResult).isEqualTo(
            Submitted(
                submitted = GuessState(
                    firstTile = LetterTileState("P".single()),
                    secondTile = LetterTileState("A".single()),
                    thirdTile = LetterTileState("P".single()),
                    fourthTile = LetterTileState("P".single()),
                    fifthTile = LetterTileState("Y".single()),
                    revealed = false
                ),
                revealed = expectedRevealedGuess,
                runningState = IN_PROGRESS
            )
        )

        assertThat(wordleGame.state.runningState).isEqualTo(IN_PROGRESS)
        assertThat(wordleGame.state.guesses.firstGuess).isEqualTo(expectedRevealedGuess)

        val expectedKeyBoardState = KeyBoardState(
            absentLetters = setOf("Y".single()),
            matchedLetters = setOf("P".single()),
            presentLetters = setOf("A".single()),
        )
        assertThat(wordleGame.state.keyBoardState).isEqualTo(expectedKeyBoardState)
    }


    @Test
    fun `submitting six wrong guesses should end the game`() {
        for(i in 1..5) {
            guessWord("PAPPY")
        }

        val guessResult = guessWord("PAPPY")
        assertThat(guessResult).isInstanceOf(Submitted::class.java)
        assertThat(guessResult as Submitted).matches { it.runningState == LOSS}
        assertThat(wordleGame.state.runningState).isEqualTo(LOSS)
    }


    @Test
    fun `updates keyboard state for different guesses`() {
        // target word = "APPLE"
        guessWord("PEARS")
        guessWord("MANGO")
        guessWord("PAPPY")
        guessWord("HAPPY")
        guessWord("ZIPPY")
        guessWord("GHOST")
        // P MATCHED, A and E Present

        assertThat(wordleGame.state.keyBoardState).isEqualTo(
            KeyBoardState(
                absentLetters = "RSMNGOYHZIT".toCharArray().toSet(),
                matchedLetters = setOf("P".single()),
                presentLetters = "AE".toCharArray().toSet(),
            )
        )
    }
}