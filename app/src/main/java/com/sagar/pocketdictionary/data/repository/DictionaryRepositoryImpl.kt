package com.sagar.pocketdictionary.data.repository

import com.sagar.pocketdictionary.data.local.dao.WordDao
import com.sagar.pocketdictionary.data.mapper.WordMapper.toDomain
import com.sagar.pocketdictionary.data.mapper.WordMapper.toEntity
import com.sagar.pocketdictionary.data.remote.DictionaryApiService
import com.sagar.pocketdictionary.domain.model.Result
import com.sagar.pocketdictionary.domain.model.Word
import com.sagar.pocketdictionary.domain.repository.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DictionaryRepositoryImpl @Inject constructor(
    private val apiService: DictionaryApiService,
    private val wordDao: WordDao
) : DictionaryRepository {

    override fun getWordDefinition(word: String): Flow<Result<Word>> = flow {
        emit(Result.Loading)

        // First, check cache
        val cachedWord = wordDao.getWord(word.lowercase())
        if (cachedWord != null) {
            emit(Result.Success(cachedWord.toDomain()))
        } else {
            // If not in cache, fetch from API
            try {
                val response = apiService.getWordDefinition(word.lowercase())
                if (response.isNotEmpty()) {
                    val domainWord = response.first().toDomain()

                    // Cache the result
                    try {
                        wordDao.insertWord(domainWord.toEntity())
                    } catch (e: Exception) {
                        // Log but don't fail if caching fails
                        e.printStackTrace()
                    }

                    emit(Result.Success(domainWord))
                } else {
                    emit(Result.Error(Exception("Word not found")))
                }
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
    }

    override fun getRecentWords(): Flow<List<Word>> {
        return wordDao.getRecentWords().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteWord(word: String) {
        wordDao.deleteWord(word.lowercase())
    }

    override suspend fun clearHistory() {
        wordDao.clearAll()
    }
}

