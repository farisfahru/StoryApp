package com.faris.storyapp.ui.activities.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.faris.storyapp.data.network.response.ListStoryItem
import com.faris.storyapp.data.repository.StoriesRepository

@ExperimentalPagingApi
class StoriesViewModel(private val storiesRepository: StoriesRepository) : ViewModel() {
    fun getUserToken(): LiveData<String?> {
        return storiesRepository.getSession()
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> =
        storiesRepository.getAllStories(token).cachedIn(viewModelScope)

    suspend fun logout() = storiesRepository.logout()

}