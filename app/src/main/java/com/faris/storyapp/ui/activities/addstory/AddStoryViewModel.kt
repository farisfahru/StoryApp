package com.faris.storyapp.ui.activities.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.faris.storyapp.data.repository.StoriesRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

@ExperimentalPagingApi
class AddStoryViewModel(private val storiesRepository: StoriesRepository) : ViewModel() {
    fun getUserToken(): LiveData<String?> {
        return storiesRepository.getSession()
    }

    fun addStory(token: String, image: MultipartBody.Part, description: RequestBody) =
        storiesRepository.addStory(token, image, description)
}