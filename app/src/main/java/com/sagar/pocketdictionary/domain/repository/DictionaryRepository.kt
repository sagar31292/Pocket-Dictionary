package com.sagar.pocketdictionary.domain.repository

import com.sagar.pocketdictionary.domain.model.Result
import com.sagar.pocketdictionary.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    fun getWordDefinition(word: String): Flow<Result<Word>>
    fun getRecentWords(): Flow<List<Word>>
    suspend fun deleteWord(word: String)
    suspend fun clearHistory()
}

