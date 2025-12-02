package com.sagar.pocketdictionary.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WordResponse(
    @Json(name = "word")
    val word: String,
    @Json(name = "phonetic")
    val phonetic: String?,
    @Json(name = "phonetics")
    val phonetics: List<Phonetic>?,
    @Json(name = "meanings")
    val meanings: List<Meaning>,
    @Json(name = "origin")
    val origin: String?
)

@JsonClass(generateAdapter = true)
data class Phonetic(
    @Json(name = "text")
    val text: String?,
    @Json(name = "audio")
    val audio: String?
)

@JsonClass(generateAdapter = true)
data class Meaning(
    @Json(name = "partOfSpeech")
    val partOfSpeech: String,
    @Json(name = "definitions")
    val definitions: List<Definition>,
    @Json(name = "synonyms")
    val synonyms: List<String>?,
    @Json(name = "antonyms")
    val antonyms: List<String>?
)

@JsonClass(generateAdapter = true)
data class Definition(
    @Json(name = "definition")
    val definition: String,
    @Json(name = "example")
    val example: String?,
    @Json(name = "synonyms")
    val synonyms: List<String>?,
    @Json(name = "antonyms")
    val antonyms: List<String>?
)

