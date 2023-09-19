package com.faris.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.faris.storyapp.data.network.api.ApiService
import com.faris.storyapp.data.Result
import com.faris.storyapp.data.network.PagingSourceFactory
import com.faris.storyapp.data.network.response.*
import com.faris.storyapp.data.preferences.UserPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoriesRepository(
    private val apiService: ApiService,
    private val userPref: UserPreferences,
) {
    fun register(name: String, email: String, pass: String): LiveData<Result<RegisterResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, pass)
                emit(Result.Success(response))
            } catch (e: Exception) {
                Log.e("TAG", "register: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun login(email: String, pass: String): LiveData<Result<LoginResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.login(email, pass)
                val token = response.loginResult.token
                userPref.saveSession(token)
                emit(Result.Success(response))

            } catch (e: Exception) {
                Log.e("TAG", "login: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun getSession(): LiveData<String?> {
        return userPref.getUserToken().asLiveData()
    }

    suspend fun logout() {
        return userPref.logout()
    }

    fun getAllStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                PagingSourceFactory(apiService, "Bearer $token")
            }
        ).liveData
    }

    fun getStoriesMaps(token: String): LiveData<Result<StoriesResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.getStoriesMaps(token = "Bearer $token", location = 1)
                emit(Result.Success(response))
            } catch (e: Exception) {
                Log.e("TAG", "getStoriesWithLocation: ${e.message.toString()}")
                emit(Result.Error(e.message.toString()))
            }
        }

    fun addStory(
        token: String,
        image: MultipartBody.Part,
        description: RequestBody,
    ): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addStory("Bearer $token", image, description)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("TAG", "addStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoriesRepository? = null

        fun getInstance(apiService: ApiService, userPref: UserPreferences): StoriesRepository =
            instance ?: synchronized(this) {
                instance ?: StoriesRepository(apiService, userPref)
            }.also { storiesRepository ->
                instance = storiesRepository
            }
    }
}