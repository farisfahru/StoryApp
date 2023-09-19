package com.faris.storyapp.ui.activities.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.faris.storyapp.data.repository.StoriesRepository
import com.faris.storyapp.data.Result
import com.faris.storyapp.data.network.response.StoriesResponse

class MapsViewModel(private val storiesRepository: StoriesRepository) : ViewModel() {
    fun getUserToken(): LiveData<String?> {
        return storiesRepository.getSession()
    }

    fun getStoriesMaps(token: String): LiveData<Result<StoriesResponse>> {
        return storiesRepository.getStoriesMaps(token)
    }
}