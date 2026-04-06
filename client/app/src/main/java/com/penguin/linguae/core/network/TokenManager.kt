package com.penguin.linguae.core.network

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    private var token: String? = null
    private const val PREFS_NAME = "linguae_prefs"
    private const val KEY_TOKEN = "access_token"
    private var sharedPreferences: SharedPreferences? = null


    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }
    fun saveToken(t: String) {
        sharedPreferences?.edit()?.putString(KEY_TOKEN, t)?.apply()
    }

    fun getToken(): String? {
        return sharedPreferences?.getString(KEY_TOKEN, null)
    }

    fun clear() {
        sharedPreferences?.edit()?.remove(KEY_TOKEN)?.apply()
    }
}