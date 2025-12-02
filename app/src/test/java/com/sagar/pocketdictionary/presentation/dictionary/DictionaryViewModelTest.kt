package com.sagar.pocketdictionary.presentation.dictionary

import com.sagar.pocketdictionary.domain.model.Result
import com.sagar.pocketdictionary.domain.model.Word
import com.sagar.pocketdictionary.domain.model.WordDefinition
import com.sagar.pocketdictionary.domain.model.WordMeaning
import com.sagar.pocketdictionary.domain.repository.DictionaryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DictionaryViewModelTest {

    private lateinit var repository: DictionaryRepository
    private lateinit var viewModel: DictionaryViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        every { repository.getRecentWords() } returns flowOf(emptyList())
        viewModel = DictionaryViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `searchWord with blank word shows error`() = runTest {
        // When
        viewModel.searchWord("")

        // Then
        assertEquals("Please enter a word", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.word)
    }

    @Test
    fun `searchWord with valid word updates state with loading then success`() = runTest {
        // Given
        val word = "test"
        val wordDefinition = WordDefinition("A test", "This is a test")
        val wordMeaning = WordMeaning("noun", listOf(wordDefinition), emptyList(), emptyList())
        val expectedWord = Word(word, "/test/", listOf(wordMeaning), null)

        every { repository.getWordDefinition(word) } returns flowOf(
            Result.Loading,
            Result.Success(expectedWord)
        )

        // When
        viewModel.searchWord(word)

        // Then
        assertEquals(expectedWord, viewModel.state.value.word)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `searchWord with API error updates state with error`() = runTest {
        // Given
        val word = "nonexistent"
        val errorMessage = "Word not found"

        every { repository.getWordDefinition(word) } returns flowOf(
            Result.Loading,
            Result.Error(Exception(errorMessage))
        )

        // When
        viewModel.searchWord(word)

        // Then
        assertEquals(errorMessage, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.word)
    }

    @Test
    fun `clearError sets error to null`() = runTest {
        // Given
        viewModel.searchWord("") // This sets an error

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `deleteWord calls repository deleteWord`() = runTest {
        // Given
        val word = "test"

        // When
        viewModel.deleteWord(word)

        // Then
        coVerify { repository.deleteWord(word) }
    }

    @Test
    fun `clearHistory calls repository clearHistory`() = runTest {
        // When
        viewModel.clearHistory()

        // Then
        coVerify { repository.clearHistory() }
    }

    @Test
    fun `recent words are loaded on initialization`() = runTest {
        // Given
        val wordDefinition = WordDefinition("A test", "This is a test")
        val wordMeaning = WordMeaning("noun", listOf(wordDefinition), emptyList(), emptyList())
        val recentWords = listOf(
            Word("hello", "/həˈloʊ/", listOf(wordMeaning), null),
            Word("world", "/wɜːrld/", listOf(wordMeaning), null)
        )

        every { repository.getRecentWords() } returns flowOf(recentWords)

        // When
        val newViewModel = DictionaryViewModel(repository)

        // Then
        assertEquals(2, newViewModel.recentWords.value.size)
        assertEquals("hello", newViewModel.recentWords.value[0].word)
        assertEquals("world", newViewModel.recentWords.value[1].word)
    }
}

