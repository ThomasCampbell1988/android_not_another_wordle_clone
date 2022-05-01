package self.tomc.nawc.game

import self.tomc.nawc.game.RunningState.*
import self.tomc.nawc.game.TileMatchState.*
import java.lang.Exception

/**
 * A single Game of Wordle
 *
 * You start with a target word, and a valid word list.
 *
 * User can have up to 6 guesses to find the target word.
 *
 * After each guess of a valid word, the user receives feedback
 * about which letters are in the correct position, present, or don't
 * exist in the target word.
 *
 * @param targetWord, five letter word which needs to be guessed
 * @param dictionary, list of valid 5 letter guesses
 * @see <a href=https://www.nytimes.com/games/wordle/index.html>Wordle</a>
 */
class WordleGame(
    private val targetWord: String,
    private val dictionary: List<String>
    ) {

    private var gameState =  WordleGameState()

    val state: WordleGameState
        get() = gameState

    fun inputLetter(letter: Char) {
        gameState = gameState.byInputtingLetter(letter)
    }

    fun deleteLetter() {
        gameState = gameState.byDeletingLastLetter()
    }

    fun submitGuess(): GuessResult {
        val activeGuess = gameState.guesses.activeGuess() ?: throw Exception("Cannot submit guess if no active guesses")
        if (!activeGuess.isFull()) return GuessResult.IncompleteWord
        if ( activeGuess.asString() !== targetWord && !dictionary.contains(activeGuess.asString()) ) return GuessResult.InvalidWord

        val tileMatchStates = resolveGuess(
            activeGuess.asString().toCharArray(),
            targetWord.toCharArray()
        )

        val revealedGuess = GuessState(
            firstTile = activeGuess.firstTile.copy(tileMatchState = tileMatchStates[0]),
            secondTile = activeGuess.secondTile.copy(tileMatchState = tileMatchStates[1]),
            thirdTile = activeGuess.thirdTile.copy(tileMatchState = tileMatchStates[2]),
            fourthTile = activeGuess.fourthTile.copy(tileMatchState = tileMatchStates[3]),
            fifthTile = activeGuess.fifthTile.copy(tileMatchState = tileMatchStates[4]),
            revealed = true
        )

        val absentLetters = mutableSetOf<Char>()
        val matchedLetters = mutableSetOf<Char>()
        val presentLetters = mutableSetOf<Char>()
        tileMatchStates.forEachIndexed { index, tileMatchState ->
            when(tileMatchState) {
                ABSENT -> absentLetters.add(activeGuess.asString()[index])
                MATCHED -> matchedLetters.add(activeGuess.asString()[index])
                PRESENT -> presentLetters.add(activeGuess.asString()[index])
                UNKNOWN -> throw Exception("All tiles should be known")
            }
        }

        gameState = gameState.byReplacingActiveGuessWith(revealedGuess)
            .byAddingAbsentKeyBoardLetters(absentLetters)
            .byAddingMatchedKeyBoardLetters(matchedLetters)
            .byAddingPresentKeyBoardLetters(presentLetters)

        if(activeGuess.asString() == targetWord) {
            gameState = gameState.copy(runningState = WON)
        } else if(gameState.guesses.activeGuess() == null) {
            gameState = gameState.copy(runningState = LOSS)
        }

        return GuessResult.Submitted(submitted = activeGuess, revealed = revealedGuess, runningState = gameState.runningState)
    }
}

sealed class GuessResult {
    object IncompleteWord : GuessResult()
    object InvalidWord : GuessResult()
    data class Submitted(
        val submitted: GuessState,
        val revealed: GuessState,
        val runningState: RunningState
        ): GuessResult()
}

data class WordleGameState(
    val guesses: Guesses = Guesses(),
    val keyBoardState: KeyBoardState = KeyBoardState(),
    val runningState: RunningState = IN_PROGRESS
) {

    fun byAddingMatchedKeyBoardLetters(letters: Set<Char>): WordleGameState {
        return this.copy(
            keyBoardState = keyBoardState.copy(
                absentLetters = this.keyBoardState.absentLetters.minus(letters),
                matchedLetters = this.keyBoardState.matchedLetters.plus(letters),
                presentLetters = this.keyBoardState.presentLetters.minus(letters),
            )
        )
    }

    fun byAddingAbsentKeyBoardLetters(letters: Set<Char>): WordleGameState {
        return this.copy(
            keyBoardState = keyBoardState.copy(
                absentLetters = this.keyBoardState.absentLetters.plus( letters.filter { !this.keyBoardState.matchedLetters.contains(it) } ),
            )
        )
    }

    fun byAddingPresentKeyBoardLetters(letters: Set<Char>): WordleGameState {
        return this.copy(
            keyBoardState = keyBoardState.copy(
                presentLetters = this.keyBoardState.presentLetters.plus( letters.filter { !this.keyBoardState.matchedLetters.contains(it) } ),
            )
        )
    }


    fun byReplacingActiveGuessWith(guessState: GuessState): WordleGameState {
        return this.copy(
            guesses = this.guesses.withActiveGuess(
                guessState = guessState
            )
        )
    }

    fun byInputtingLetter(letter: Char): WordleGameState {
        val activeGuess = this.guesses.activeGuess() ?: return this
        val activeTile =  activeGuess.activeTile() ?: return this

        return this.copy(
            guesses = this.guesses.withActiveGuess(
                guessState = activeGuess.withActiveTile(
                    letterTileState = activeTile.copy(letter = letter)
                )
            )
        )
    }

    fun byDeletingLastLetter(): WordleGameState {
        val activeGuess = this.guesses.activeGuess() ?: return this
        activeGuess.previousActiveTile() ?: return this

        return this.copy(
            guesses = this.guesses.withActiveGuess(
                guessState = activeGuess.withLastTileDeleted()
            )
        )
    }
}

data class Guesses(
    val firstGuess: GuessState = GuessState(),
    val secondGuess: GuessState = GuessState(),
    val thirdGuess: GuessState = GuessState(),
    val fourthGuess: GuessState = GuessState(),
    val fifthGuess: GuessState = GuessState(),
    val sixthGuess: GuessState = GuessState()
) {
    fun asList() = listOf(
        firstGuess,
        secondGuess,
        thirdGuess,
        fourthGuess,
        fifthGuess,
        sixthGuess,
    )

    fun activeGuess(): GuessState? = asList().firstOrNull { !it.revealed }

    fun withActiveGuess(guessState: GuessState): Guesses {
        return when(asList().indexOfFirst { !it.revealed  }) {
            0 -> this.copy(firstGuess = guessState)
            1 -> this.copy(secondGuess = guessState)
            2 -> this.copy(thirdGuess = guessState)
            3 -> this.copy(fourthGuess = guessState)
            4 -> this.copy(fifthGuess = guessState)
            5 -> this.copy(sixthGuess = guessState)
            else -> throw Exception("No unrevealed guess to replace")
        }
    }
}

data class GuessState(
    val firstTile: LetterTileState = LetterTileState(),
    val secondTile: LetterTileState = LetterTileState(),
    val thirdTile: LetterTileState = LetterTileState(),
    val fourthTile: LetterTileState = LetterTileState(),
    val fifthTile: LetterTileState = LetterTileState(),
    val revealed: Boolean = false
) {

    fun isFull() = lettersAsList().all { it.letter != null }

    fun asString() = lettersAsList().filter { it.letter != null }.map { it.letter }.joinToString("")

    fun asOptionalCharList(): List<Char?> = lettersAsList().map { it.letter }

    fun lettersAsList() = listOf(
        firstTile,
        secondTile,
        thirdTile,
        fourthTile,
        fifthTile
    )

    fun activeTile() = lettersAsList().firstOrNull { it.letter == null }

    fun previousActiveTile() = lettersAsList().lastOrNull { it.letter != null }

    fun withActiveTile(letterTileState: LetterTileState): GuessState {
        return when(lettersAsList().indexOfFirst { it.letter == null }) {
            0 -> this.copy(firstTile = letterTileState)
            1 -> this.copy(secondTile = letterTileState)
            2 -> this.copy(thirdTile = letterTileState)
            3 -> this.copy(fourthTile = letterTileState)
            4 -> this.copy(fifthTile = letterTileState)
            else -> throw Exception("No blank tile to replace")
        }
    }

    fun withLastTileDeleted(): GuessState {
        return when(lettersAsList().indexOfLast { it.letter != null }) {
            0 -> this.copy(firstTile = LetterTileState())
            1 -> this.copy(secondTile = LetterTileState())
            2 -> this.copy(thirdTile = LetterTileState())
            3 -> this.copy(fourthTile = LetterTileState())
            4 -> this.copy(fifthTile = LetterTileState())
            else -> throw Exception("No filled tile to replace")
        }
    }
}

data class LetterTileState(
    val letter: Char? = null,
    val tileMatchState: TileMatchState = UNKNOWN,
)

enum class TileMatchState {
    UNKNOWN, MATCHED, PRESENT, ABSENT
}

/**
 * Store which guessed letters are absent, matched or present in the
 * target word.
 *
 * If a letter is both matched, and present, it should only appear
 * in the matchedLetters list.
 */
data class KeyBoardState(
    val absentLetters: Set<Char> = emptySet(),
    val matchedLetters: Set<Char> = emptySet(),
    val presentLetters: Set<Char> = emptySet()
)

enum class RunningState {
    IN_PROGRESS, WON, LOSS
}

fun resolveGuess(guess: CharArray, target: CharArray): List<TileMatchState> {
    if( guess.size != 5 || target.size != 5) throw Exception("guess and target must both be 5 letters")

    val result = mutableListOf(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN)
    val unmatchedIndexes: MutableList<Int> = mutableListOf()
    val unmatchedTargetLetters: MutableList<Char> = mutableListOf()
    for(index in 0..4) {
        val guessLetter = guess[index]
        val targetLetter = target[index]
        if(targetLetter == guessLetter) {
            result[index] = MATCHED
        } else {
            unmatchedIndexes.add(index)
            unmatchedTargetLetters.add(targetLetter)
        }
    }

    for(unmatchedIndex in unmatchedIndexes) {
        val guessLetter = guess[unmatchedIndex]
        if(unmatchedTargetLetters.contains(guessLetter)) {
            result[unmatchedIndex] = PRESENT
            unmatchedTargetLetters.remove(guessLetter)
        } else {
            result[unmatchedIndex] = ABSENT
        }
    }

    return result
}