package com.example.chatbot.di

import com.example.chatbot.data.remote.GeminiHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGeminiHelper(): GeminiHelper {
        return GeminiHelper()
    }
}