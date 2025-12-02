package com.sagar.pocketdictionary.domain.model

data class Word(
    val word: String,
    val phonetic: String?,
    val meanings: List<WordMeaning>,
    val origin: String?
)

data class WordMeaning(
    val partOfSpeech: String,
    val definitions: List<WordDefinition>,
    val synonyms: List<String>,
    val antonyms: List<String>
)

data class WordDefinition(
    val definition: String,
    val example: String?
)

