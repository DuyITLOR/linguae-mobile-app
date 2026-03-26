package com.penguin.linguae.core.network

import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

//    private const val BASE_URL = "http://10.0.2.2:5050/"
    private  const val BASE_URL = "http://192.168.1.21:5050/"
    private const val API_KEY = "dev-local-key"


    // interceptor gắn token + api key
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        // thêm api key
        requestBuilder.addHeader("x-api-key", API_KEY)

        // thêm token nếu có
        TokenManager.getToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        chain.proceed(requestBuilder.build())
    }

    // logging
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client) //  QUAN TRỌNG
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(api: Class<T>): T {
        return retrofit.create(api)
    }
}