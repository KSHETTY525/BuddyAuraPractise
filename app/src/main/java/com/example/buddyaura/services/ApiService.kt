package com.example.buddyaura.services

import com.example.buddyaura.data.FeedResponse
import com.example.buddyaura.data.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @GET("v1/feed/main")
    suspend fun getHomeFeed(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String?
    ): FeedResponse

    @Multipart
    @POST("uploadProfile")
    suspend fun uploadProfilePic(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @GET("getProfileImage")
    suspend fun getProfileImage(): Response<UploadResponse>
}
