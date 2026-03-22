package com.penguin.linguae.core.network

import com.penguin.linguae.core.network.api.VocabularyApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3306/"

    private val retrofit = Retrofit.Builder()
        .baseUrl((BASE_URL))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val vocabularyApi: VocabularyApi by lazy {
        retrofit.create(VocabularyApi::class.java)
    }

//    val columnApi: ColumnApi by lazy {
//        retrofit.create(ColumnApi::class.java)
//    }
//
//    val userApi: UserApi by lazy {
//        retrofit.create(UserApi::class.java)
//    }
//
//    val authApi: AuthApi by lazy {
//        retrofit.create(AuthApi::class.java)
//    }
}