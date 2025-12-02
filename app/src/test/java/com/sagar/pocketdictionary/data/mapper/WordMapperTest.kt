package com.sagar.pocketdictionary.data.mapper

import com.sagar.pocketdictionary.data.local.entity.WordEntity
import com.sagar.pocketdictionary.data.mapper.WordMapper.toDomain
import com.sagar.pocketdictionary.data.mapper.WordMapper.toEntity
import com.sagar.pocketdictionary.data.remote.model.Definition
import com.sagar.pocketdictionary.data.remote.model.Meaning
import com.sagar.pocketdictionary.data.remote.model.Phonetic
import com.sagar.pocketdictionary.data.remote.model.WordResponse
import com.sagar.pocketdictionary.domain.model.Word
import com.sagar.pocketdictionary.domain.model.WordDefinition
import com.sagar.pocketdictionary.domain.model.WordMeaning
import org.junit.Assert.*
import org.junit.Test

class WordMapperTest {

    @Test
    fun `WordResponse to Domain maps correctly`() {
        // Given
        val definition = Definition(
            definition = "A greeting",
            example = "Hello world",
            synonyms = listOf("hi", "hey"),
            antonyms = listOf("goodbye")
        )
        val meaning = Meaning(
            partOfSpeech = "noun",
            definitions = listOf(definition),
            synonyms = listOf("greeting"),
            antonyms = emptyList()
        )
        val phonetic = Phonetic(text = "/həˈloʊ/", audio = "audio.mp3")
        val wordResponse = WordResponse(
            word = "hello",
            phonetic = "/həˈloʊ/",
            phonetics = listOf(phonetic),
            meanings = listOf(meaning),
            origin = "Old English"
        )

        // When
        val domain = wordResponse.toDomain()

        // Then
        assertEquals("hello", domain.word)
        assertEquals("/həˈloʊ/", domain.phonetic)
        assertEquals("Old English", domain.origin)
        assertEquals(1, domain.meanings.size)
        assertEquals("noun", domain.meanings[0].partOfSpeech)
        assertEquals(1, domain.meanings[0].definitions.size)
        assertEquals("A greeting", domain.meanings[0].definitions[0].definition)
        assertEquals("Hello world", domain.meanings[0].definitions[0].example)
        assertEquals(listOf("greeting"), domain.meanings[0].synonyms)
    }

    @Test
    fun `WordResponse without phonetic uses phonetics array`() {
        // Given
        val phonetic = Phonetic(text = "/test/", audio = null)
        val wordResponse = WordResponse(
            word = "test",
            phonetic = null,
            phonetics = listOf(phonetic),
            meanings = listOf(
                Meaning(
                    partOfSpeech = "noun",
                    definitions = listOf(
                        Definition("A test", null, null, null)
                    ),
                    synonyms = null,
                    antonyms = null
                )
            ),
            origin = null
        )

        // When
        val domain = wordResponse.toDomain()

        // Then
        assertEquals("/test/", domain.phonetic)
    }

    @Test
    fun `Domain Word to Entity maps correctly`() {
        // Given
        val wordDefinition = WordDefinition("A greeting", "Hello world")
        val wordMeaning = WordMeaning(
            "noun",
            listOf(wordDefinition),
            listOf("hi"),
            emptyList()
        )
        val word = Word("hello", "/həˈloʊ/", listOf(wordMeaning), "Old English")

        // When
        val entity = word.toEntity()

        // Then
        assertEquals("hello", entity.word)
        assertEquals("/həˈloʊ/", entity.phonetic)
        assertEquals("Old English", entity.origin)
        assertTrue(entity.meanings.isNotEmpty())
        assertTrue(entity.timestamp > 0)
    }

    @Test
    fun `WordEntity to Domain maps correctly`() {
        // Given
        val meanings = """[{"partOfSpeech":"noun","definitions":[{"definition":"A greeting","example":"Hello world"}],"synonyms":["hi"],"antonyms":[]}]"""
        val entity = WordEntity(
            word = "hello",
            phonetic = "/həˈloʊ/",
            meanings = meanings,
            origin = "Old English",
            timestamp = System.currentTimeMillis()
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("hello", domain.word)
        assertEquals("/həˈloʊ/", domain.phonetic)
        assertEquals("Old English", domain.origin)
        assertEquals(1, domain.meanings.size)
        assertEquals("noun", domain.meanings[0].partOfSpeech)
    }

    @Test
    fun `WordEntity with invalid JSON returns empty meanings`() {
        // Given
        val entity = WordEntity(
            word = "test",
            phonetic = "/test/",
            meanings = "invalid json",
            origin = null,
            timestamp = System.currentTimeMillis()
        )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals("test", domain.word)
        assertTrue(domain.meanings.isEmpty())
    }

    @Test
    fun `WordResponse with null synonyms and antonyms maps to empty lists`() {
        // Given
        val wordResponse = WordResponse(
            word = "test",
            phonetic = null,
            phonetics = null,
            meanings = listOf(
                Meaning(
                    partOfSpeech = "noun",
                    definitions = listOf(
                        Definition("A test", null, null, null)
                    ),
                    synonyms = null,
                    antonyms = null
                )
            ),
            origin = null
        )

        // When
        val domain = wordResponse.toDomain()

        // Then
        assertTrue(domain.meanings[0].synonyms.isEmpty())
        assertTrue(domain.meanings[0].antonyms.isEmpty())
    }
}

