package com.penguin.linguae.core.network

import com.penguin.linguae.data.model.User

object UserManager {
    private var user: User? = null

    fun saveUser(user: User) {
        this.user = user
    }

    fun getUser(): User? {
        return user
    }

    fun clearUser() {
        this.user = null
    }
}