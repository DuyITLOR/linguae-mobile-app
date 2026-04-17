package com.penguin.linguae.core.network

import com.penguin.linguae.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

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
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .callTimeout(90, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client) //  QUAN TRỌNG
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(api: Class<T>): T {
        return retrofit.create(api)
    }
}
