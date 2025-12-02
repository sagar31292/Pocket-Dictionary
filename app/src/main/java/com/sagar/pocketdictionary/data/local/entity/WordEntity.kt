package com.sagar.pocketdictionary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey
    val word: String,
    val phonetic: String?,
    val meanings: String, // JSON string
    val origin: String?,
    val timestamp: Long
)

