package com.sagar.pocketdictionary.data.remote

import com.sagar.pocketdictionary.data.remote.model.WordResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {
    @GET("api/v2/entries/en/{word}")
    suspend fun getWordDefinition(@Path("word") word: String): List<WordResponse>
}

