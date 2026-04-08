package com.penguin.linguae.core.network

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.penguin.linguae.data.model.User

object UserManager {
    private const val PREFS_NAME = "linguae_prefs"
    private const val KEY_USER = "current_user"

    private var user: User? = null
    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()

    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }

        if (user == null) {
            val savedUser = sharedPreferences?.getString(KEY_USER, null)
            user = savedUser?.let {
                runCatching { gson.fromJson(it, User::class.java) }.getOrNull()
            }
        }
    }

    fun saveUser(user: User) {
        this.user = user
        sharedPreferences?.edit()?.putString(KEY_USER, gson.toJson(user))?.apply()
    }

    fun getUser(): User? {
        if (user == null) {
            val savedUser = sharedPreferences?.getString(KEY_USER, null)
            user = savedUser?.let {
                runCatching { gson.fromJson(it, User::class.java) }.getOrNull()
            }
        }
        return user
    }

    fun clearUser() {
        user = null
        sharedPreferences?.edit()?.remove(KEY_USER)?.apply()
    }
}
