package com.sagar.pocketdictionary.data.repository

import app.cash.turbine.test
import com.sagar.pocketdictionary.data.local.dao.WordDao
import com.sagar.pocketdictionary.data.local.entity.WordEntity
import com.sagar.pocketdictionary.data.mapper.WordMapper.toDomain
import com.sagar.pocketdictionary.data.remote.DictionaryApiService
import com.sagar.pocketdictionary.data.remote.model.Definition
import com.sagar.pocketdictionary.data.remote.model.Meaning
import com.sagar.pocketdictionary.data.remote.model.WordResponse
import com.sagar.pocketdictionary.domain.model.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DictionaryRepositoryImplTest {

    private lateinit var apiService: DictionaryApiService
    private lateinit var wordDao: WordDao
    private lateinit var repository: DictionaryRepositoryImpl

    @Before
    fun setup() {
        apiService = mockk()
        wordDao = mockk(relaxed = true)
        repository = DictionaryRepositoryImpl(apiService, wordDao)
    }

    @Test
    fun `getWordDefinition returns cached word when available`() = runTest {
        // Given
        val word = "test"
        val cachedEntity = WordEntity(
            word = word,
            phonetic = "/test/",
            meanings = "[]",
            origin = "test origin",
            timestamp = System.currentTimeMillis()
        )
        coEvery { wordDao.getWord(word) } returns cachedEntity

        // When & Then
        repository.getWordDefinition(word).test {
            assertTrue(awaitItem() is Result.Loading)
            val item = awaitItem()
            assertTrue(item is Result.Success)
            assertEquals(word, (item as Result.Success).data.word)
            awaitComplete()
        }

        coVerify(exactly = 0) { apiService.getWordDefinition(any()) }
    }

    @Test
    fun `getWordDefinition fetches from API when not cached`() = runTest {
        // Given
        val word = "hello"
        val definition = Definition(
            definition = "A greeting",
            example = "Hello world",
            synonyms = null,
            antonyms = null
        )
        val meaning = Meaning(
            partOfSpeech = "noun",
            definitions = listOf(definition),
            synonyms = null,
            antonyms = null
        )
        val apiResponse = WordResponse(
            word = word,
            phonetic = "/həˈloʊ/",
            phonetics = null,
            meanings = listOf(meaning),
            origin = null
        )

        coEvery { wordDao.getWord(word) } returns null
        coEvery { wordDao.insertWord(any()) } returns Unit
        coEvery { apiService.getWordDefinition(word) } returns listOf(apiResponse)

        // When & Then
        repository.getWordDefinition(word).test {
            assertTrue(awaitItem() is Result.Loading)
            val item = awaitItem()
            assertTrue(item is Result.Success)
            val successResult = item as Result.Success
            assertEquals(word, successResult.data.word)
            assertEquals("/həˈloʊ/", successResult.data.phonetic)
            awaitComplete()
        }

        coVerify { apiService.getWordDefinition(word) }
    }

    @Test
    fun `getWordDefinition returns error when API fails and no cache`() = runTest {
        // Given
        val word = "nonexistent"
        coEvery { wordDao.getWord(word) } returns null
        coEvery { apiService.getWordDefinition(word) } throws Exception("Network error")

        // When & Then
        repository.getWordDefinition(word).test {
            assertTrue(awaitItem() is Result.Loading)
            val item = awaitItem()
            assertTrue(item is Result.Error)
            assertEquals("Network error", (item as Result.Error).exception.message)
            awaitComplete()
        }
    }

    @Test
    fun `getWordDefinition returns cached data when available without calling API`() = runTest {
        // Given
        val word = "test"
        val cachedEntity = WordEntity(
            word = word,
            phonetic = "/test/",
            meanings = "[]",
            origin = null,
            timestamp = System.currentTimeMillis()
        )
        coEvery { wordDao.getWord(word) } returns cachedEntity

        // When & Then
        repository.getWordDefinition(word).test {
            assertTrue(awaitItem() is Result.Loading)
            val item = awaitItem()
            assertTrue(item is Result.Success)
            assertEquals(word, (item as Result.Success).data.word)
            awaitComplete()
        }

        // API should not be called when cache is available
        coVerify(exactly = 0) { apiService.getWordDefinition(any()) }
    }

    @Test
    fun `deleteWord calls dao deleteWord`() = runTest {
        // Given
        val word = "test"

        // When
        repository.deleteWord(word)

        // Then
        coVerify { wordDao.deleteWord(word) }
    }

    @Test
    fun `clearHistory calls dao clearAll`() = runTest {
        // When
        repository.clearHistory()

        // Then
        coVerify { wordDao.clearAll() }
    }
}

