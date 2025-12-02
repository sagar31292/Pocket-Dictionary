package com.sagar.pocketdictionary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sagar.pocketdictionary.data.local.dao.WordDao
import com.sagar.pocketdictionary.data.local.entity.WordEntity

@Database(
    entities = [WordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

