package com.faris.storyapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.faris.storyapp.utils.USER_TOKEN
import com.faris.storyapp.utils.preferenceName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = preferenceName)

class UserPreferences(private val dataStore: DataStore<Preferences>) {
    fun getUserToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_TOKEN]
    }

    suspend fun saveSession(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it.clear()
        }
    }
}