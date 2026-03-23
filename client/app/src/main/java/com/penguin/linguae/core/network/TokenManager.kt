package com.penguin.linguae.core.network

object TokenManager {

    private var token: String? = null

    fun saveToken(t: String) {
        token = t
    }

    fun getToken(): String? {
        return token
    }

    fun clear() {
        token = null
    }
}