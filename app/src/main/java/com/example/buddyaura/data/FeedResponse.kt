package com.example.buddyaura.data

data class FeedResponse(
    val success: Boolean,
    val statusCode: Int,
    val message: String,
    val data: FeedData
)


