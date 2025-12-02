package com.sagar.pocketdictionary.data.mapper

import com.sagar.pocketdictionary.data.local.entity.WordEntity
import com.sagar.pocketdictionary.data.remote.model.WordResponse
import com.sagar.pocketdictionary.domain.model.Word
import com.sagar.pocketdictionary.domain.model.WordDefinition
import com.sagar.pocketdictionary.domain.model.WordMeaning
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object WordMapper {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val meaningListType = Types.newParameterizedType(List::class.java, WordMeaning::class.java)
    private val meaningListAdapter = moshi.adapter<List<WordMeaning>>(meaningListType)

    fun WordResponse.toDomain(): Word {
        return Word(
            word = word,
            phonetic = phonetic ?: phonetics?.firstOrNull()?.text,
            meanings = meanings.map { meaning ->
                WordMeaning(
                    partOfSpeech = meaning.partOfSpeech,
                    definitions = meaning.definitions.map { def ->
                        WordDefinition(
                            definition = def.definition,
                            example = def.example
                        )
                    },
                    synonyms = meaning.synonyms ?: emptyList(),
                    antonyms = meaning.antonyms ?: emptyList()
                )
            },
            origin = origin
        )
    }

    fun Word.toEntity(): WordEntity {
        return WordEntity(
            word = word,
            phonetic = phonetic,
            meanings = meaningListAdapter.toJson(meanings),
            origin = origin,
            timestamp = System.currentTimeMillis()
        )
    }

    fun WordEntity.toDomain(): Word {
        val meanings = try {
            meaningListAdapter.fromJson(meanings) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        return Word(
            word = word,
            phonetic = phonetic,
            meanings = meanings,
            origin = origin
        )
    }
}

