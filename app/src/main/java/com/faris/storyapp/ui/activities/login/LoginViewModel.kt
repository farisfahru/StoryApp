package com.faris.storyapp.ui.activities.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.faris.storyapp.data.repository.StoriesRepository

class LoginViewModel(private val storiesRepository: StoriesRepository) : ViewModel() {
    fun getUserToken(): LiveData<String?> {
        return storiesRepository.getSession()
    }

    fun login(email: String, password: String) =
        storiesRepository.login(email, password)
}