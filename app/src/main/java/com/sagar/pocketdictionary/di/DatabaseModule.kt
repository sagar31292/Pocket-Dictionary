package com.sagar.pocketdictionary.di

import android.content.Context
import androidx.room.Room
import com.sagar.pocketdictionary.data.local.DictionaryDatabase
import com.sagar.pocketdictionary.data.local.dao.WordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDictionaryDatabase(
        @ApplicationContext context: Context
    ): DictionaryDatabase {
        return Room.databaseBuilder(
            context,
            DictionaryDatabase::class.java,
            "dictionary_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWordDao(database: DictionaryDatabase): WordDao {
        return database.wordDao()
    }
}

