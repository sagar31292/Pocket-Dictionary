package com.sagar.pocketdictionary.presentation.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagar.pocketdictionary.domain.model.Result
import com.sagar.pocketdictionary.domain.model.Word
import com.sagar.pocketdictionary.domain.repository.DictionaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val repository: DictionaryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DictionaryState())
    val state: StateFlow<DictionaryState> = _state.asStateFlow()

    private val _recentWords = MutableStateFlow<List<Word>>(emptyList())
    val recentWords: StateFlow<List<Word>> = _recentWords.asStateFlow()

    init {
        loadRecentWords()
    }

    fun searchWord(word: String) {
        if (word.isBlank()) {
            _state.value = DictionaryState(error = "Please enter a word")
            return
        }

        repository.getWordDefinition(word)
            .onEach { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.value = DictionaryState(isLoading = true)
                    }
                    is Result.Success -> {
                        _state.value = DictionaryState(word = result.data)
                        loadRecentWords()
                    }
                    is Result.Error -> {
                        _state.value = DictionaryState(
                            error = result.exception.message ?: "An unexpected error occurred"
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun loadRecentWords() {
        repository.getRecentWords()
            .onEach { words ->
                _recentWords.value = words
            }
            .launchIn(viewModelScope)
    }

    fun deleteWord(word: String) {
        viewModelScope.launch {
            repository.deleteWord(word)
            loadRecentWords()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _recentWords.value = emptyList()
        }
    }
}

data class DictionaryState(
    val word: Word? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

