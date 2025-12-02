package com.sagar.pocketdictionary

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun app_launches_successfully() {
        // Verify the app launches and shows the main screen
        composeTestRule.onNodeWithText("Pocket Dictionary").assertExists()
        composeTestRule.onNodeWithText("Search for a word...").assertExists()
    }

    @Test
    fun fullUserFlow_searchAndViewWord() {
        // 1. Start with empty state
        composeTestRule.onNodeWithText("Recent Searches").assertExists()

        // 2. Type a word
        composeTestRule.onNodeWithText("Search for a word...")
            .performTextInput("hello")

        // 3. Trigger search
        composeTestRule.onNodeWithText("Search for a word...")
            .performImeAction()

        // 4. Wait for results (or error)
        composeTestRule.waitForIdle()

        // The test will pass if no crashes occur
        // In a real test with mocked network, we would verify specific content
    }

    @Test
    fun userFlow_typeAndClearSearch() {
        // Type a word
        composeTestRule.onNodeWithText("Search for a word...")
            .performTextInput("test word")

        // Verify clear button appears and works
        composeTestRule.onNodeWithContentDescription("Clear")
            .assertExists()
            .performClick()

        // Verify input is cleared
        composeTestRule.onNodeWithText("Search for a word...")
            .assertExists()
    }

    @Test
    fun userFlow_navigateToRecentSearches() {
        // The app should show recent searches by default when no word is searched
        composeTestRule.onNodeWithText("Recent Searches").assertExists()
    }
}

