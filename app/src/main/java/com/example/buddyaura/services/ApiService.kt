package com.example.buddyaura.services

import com.example.buddyaura.data.FeedResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("v1/feed/main")
    suspend fun getHomeFeed(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String?
    ): FeedResponse

}
