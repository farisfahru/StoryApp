package com.faris.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import com.faris.storyapp.data.repository.StoriesRepository
import com.faris.storyapp.di.Injection
import com.faris.storyapp.ui.activities.addstory.AddStoryViewModel
import com.faris.storyapp.ui.activities.home.StoriesViewModel
import com.faris.storyapp.ui.activities.login.LoginViewModel
import com.faris.storyapp.ui.activities.maps.MapsViewModel
import com.faris.storyapp.ui.activities.register.RegisterViewModel


class ViewModelFactory private constructor(private val storiesRepository: StoriesRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @OptIn(ExperimentalPagingApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(storiesRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(storiesRepository) as T
            }
            modelClass.isAssignableFrom(StoriesViewModel::class.java) -> {
                StoriesViewModel(storiesRepository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(storiesRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storiesRepository) as T
            }
            else -> throw  Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { viewModelFactory ->
                instance = viewModelFactory
            }
    }
}