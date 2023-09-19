package com.faris.storyapp.ui.activities.register

import androidx.lifecycle.ViewModel
import com.faris.storyapp.data.repository.StoriesRepository

class RegisterViewModel(private val repository: StoriesRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)
}