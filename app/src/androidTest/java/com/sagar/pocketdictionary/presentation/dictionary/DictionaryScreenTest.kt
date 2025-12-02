package com.sagar.pocketdictionary.presentation.dictionary

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.sagar.pocketdictionary.MainActivity
import com.sagar.pocketdictionary.data.local.dao.WordDao
import com.sagar.pocketdictionary.data.remote.DictionaryApiService
import com.sagar.pocketdictionary.data.remote.model.Definition
import com.sagar.pocketdictionary.data.remote.model.Meaning
import com.sagar.pocketdictionary.data.remote.model.WordResponse
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class DictionaryScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var apiService: DictionaryApiService

    @Inject
    lateinit var wordDao: WordDao

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun dictionaryScreen_displaysSearchBar() {
        composeTestRule.onNodeWithText("Search for a word...").assertExists()
    }

    @Test
    fun dictionaryScreen_displaysTitle() {
        composeTestRule.onNodeWithText("Pocket Dictionary").assertExists()
    }

    @Test
    fun dictionaryScreen_showsRecentSearchesWhenEmpty() {
        composeTestRule.onNodeWithText("Recent Searches").assertExists()
        composeTestRule.onNodeWithText("No recent searches").assertExists()
    }

    @Test
    fun dictionaryScreen_searchingForWord_showsLoading() = runTest {
        // Setup mock response
        val wordResponse = WordResponse(
            word = "hello",
            phonetic = "/həˈloʊ/",
            phonetics = null,
            meanings = listOf(
                Meaning(
                    partOfSpeech = "noun",
                    definitions = listOf(
                        Definition(
                            definition = "A greeting",
                            example = "Hello world",
                            synonyms = null,
                            antonyms = null
                        )
                    ),
                    synonyms = null,
                    antonyms = null
                )
            ),
            origin = null
        )

        coEvery { wordDao.getWord("hello") } returns null
        coEvery { apiService.getWordDefinition("hello") } returns listOf(wordResponse)

        // Type in search box
        composeTestRule.onNodeWithText("Search for a word...")
            .performTextInput("hello")

        // Press search (IME action)
        composeTestRule.onNodeWithText("Search for a word...")
            .performImeAction()

        // Verify loading or result appears
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("hello")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun dictionaryScreen_searchButton_clearsText() {
        // Type text
        composeTestRule.onNodeWithText("Search for a word...")
            .performTextInput("test")

        // Click clear button
        composeTestRule.onNodeWithContentDescription("Clear")
            .performClick()

        // Verify text is cleared
        composeTestRule.onNodeWithText("Search for a word...")
            .assertExists()
    }

    @Test
    fun dictionaryScreen_errorMessage_canBeDismissed() {
        // Type blank and search
        composeTestRule.onNodeWithText("Search for a word...")
            .performImeAction()

        // Wait for error
        composeTestRule.waitForIdle()

        // Check if error exists
        composeTestRule.onNodeWithText("Error").assertExists()

        // Dismiss error
        composeTestRule.onNodeWithText("Dismiss").performClick()

        // Error should be gone
        composeTestRule.onNodeWithText("Error").assertDoesNotExist()
    }
}

