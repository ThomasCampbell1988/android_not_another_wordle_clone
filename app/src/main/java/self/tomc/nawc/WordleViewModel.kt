package self.tomc.nawc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import self.tomc.nawc.data.DictionaryProvider
import self.tomc.nawc.game.*

class WordleViewModel(dictionaryProvider: DictionaryProvider): ViewModel() {

    private val game: WordleGame = WordleGame("APPLE", dictionaryProvider.getWordDictionary())

    private val _gameState = MutableStateFlow(game.state)
    val gameState: StateFlow<WordleGameState>
        get() = _gameState

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState>
        get() = _uiState

    fun inputLetter(letter: Char) {
        if(game.state.runningState != RunningState.IN_PROGRESS || _uiState.value.revealRow != null) return
        game.inputLetter(letter)
        _gameState.value = game.state
    }

    fun delete() {
        if(game.state.runningState != RunningState.IN_PROGRESS || _uiState.value.revealRow != null) return
        game.deleteLetter()
        _gameState.value = game.state
    }

    fun enter() {
        if(game.state.runningState != RunningState.IN_PROGRESS || _uiState.value.revealRow != null) return
        val guessResult = game.submitGuess()
        when(guessResult) {
            GuessResult.IncompleteWord -> { } // do nothing
            GuessResult.InvalidWord -> { _uiState.value = _uiState.value.copy(showInvalidWordMessage = true) }
            is GuessResult.Submitted -> { _uiState.value = _uiState.value.copy(revealRow = guessResult.revealed)}
        }
    }

    fun haveShownInvalidWordMessage() {
        _uiState.value = _uiState.value.copy(showInvalidWordMessage = false)
    }

    fun haveRevealedRow() {
        _uiState.value = _uiState.value.copy(revealRow = null)
        _gameState.value = game.state
    }


    class WordleViewModelFactory (
        private val dictionaryProvider: DictionaryProvider
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) =
            (WordleViewModel(dictionaryProvider) as T)
    }
}

data class UIState(
    val showInvalidWordMessage: Boolean = false,
    val revealRow: GuessState? = null
)