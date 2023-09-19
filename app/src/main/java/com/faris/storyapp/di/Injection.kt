package com.faris.storyapp.di

import android.content.Context
import com.faris.storyapp.data.network.api.ApiConfig
import com.faris.storyapp.data.preferences.UserPreferences
import com.faris.storyapp.data.preferences.dataStore
import com.faris.storyapp.data.repository.StoriesRepository

object Injection {
    fun provideRepository(context: Context): StoriesRepository {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences(context.dataStore)
        return StoriesRepository.getInstance(apiService, userPreferences)
    }
}