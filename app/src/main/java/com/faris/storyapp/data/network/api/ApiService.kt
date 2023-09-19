package com.faris.storyapp.data.network.api

import com.faris.storyapp.data.network.response.AddStoryResponse
import com.faris.storyapp.data.network.response.LoginResponse
import com.faris.storyapp.data.network.response.RegisterResponse
import com.faris.storyapp.data.network.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int? = null,
    ): StoriesResponse

    @GET("stories")
    suspend fun getStoriesMaps(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1,
    ): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): AddStoryResponse
}