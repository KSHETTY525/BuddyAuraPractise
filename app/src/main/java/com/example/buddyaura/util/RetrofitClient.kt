package com.example.buddyaura.util

import com.example.buddyaura.services.ApiService
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val MAIN_BASE_URL = "https://api.buddyaura.com/api/"
    private const val UPLOAD_BASE_URL = "http://10.0.2.2:3000/"

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    val api: ApiService = Retrofit.Builder()
        .baseUrl(MAIN_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    val uploadApi: ApiService = Retrofit.Builder()
        .baseUrl(UPLOAD_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}
