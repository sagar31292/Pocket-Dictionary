package com.sagar.pocketdictionary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sagar.pocketdictionary.presentation.dictionary.DictionaryScreen
import com.sagar.pocketdictionary.ui.theme.PocketDictionaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketDictionaryTheme {
                DictionaryScreen()
            }
        }
    }
}
