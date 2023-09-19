package com.faris.storyapp

import com.faris.storyapp.data.network.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {
    fun generateDummyStoriesList(): List<ListStoryItem> {
        val storiesList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "Photo URL",
                "2023-13-07T06:34:18.598Z",
                "Faris",
                "Ini Caption",
                -10.212,
                "id",
                -16.002
            )
            storiesList.add(story)
        }
        return storiesList
    }

    fun generateLoginResult(): LoginResponse {
        val loginResult = LoginResult("Faris", "user001", "token")

        return LoginResponse(
            loginResult,
            false,
            "success"
        )
    }

    fun generateRegister(): RegisterResponse{
        return RegisterResponse(false, "success")
    }
    fun generateAddStory(): AddStoryResponse {
        return AddStoryResponse(
            false,
            "success"
        )
    }
    fun generateMultipart(): MultipartBody.Part {
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }
    fun generateRequestBody(): RequestBody {
        val dummyText = "text"
        return dummyText.toRequestBody()
    }

    fun generateDummyStoriesResponse(): StoriesResponse {
        val error = false
        val message = "Stories fetched successfully"
        val storiesList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "Photo URL",
                "2023-13-07T06:34:18.598Z",
                "Faris",
                "Ini Caption",
                -10.212,
                "id",
                -16.002
            )
            storiesList.add(story)
        }
        return StoriesResponse(storiesList, error, message)
    }
}