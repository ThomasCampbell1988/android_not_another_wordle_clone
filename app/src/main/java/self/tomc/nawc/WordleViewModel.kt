package self.tomc.nawc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import self.tomc.nawc.data.DictionaryProvider
import self.tomc.nawc.game.RunningState
import self.tomc.nawc.game.WordleGame
import self.tomc.nawc.game.WordleGameState

class WordleViewModel(dictionaryProvider: DictionaryProvider): ViewModel() {

    private val game: WordleGame = WordleGame("APPLE", dictionaryProvider.getWordDictionary())

    private val _gameState = MutableStateFlow(game.state)
    val gameState: StateFlow<WordleGameState>
        get() = _gameState

    fun inputLetter(letter: Char) {
        if(game.state.runningState != RunningState.IN_PROGRESS) return
        game.inputLetter(letter)
        _gameState.value = game.state
    }

    fun delete() {
        if(game.state.runningState != RunningState.IN_PROGRESS) return
        game.deleteLetter()
        _gameState.value = game.state
    }

    fun enter() {
        if(game.state.runningState != RunningState.IN_PROGRESS) return
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