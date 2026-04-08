package com.penguin.linguae.data.model

import com.google.gson.annotations.SerializedName

data class ClozeOption(
    val id: Int = -1,
    val questionId: Int = -1,
    @SerializedName("optionText") val answer: String = "",
    val isCorrect: Boolean? = null,
    val onClick: () -> Unit = {}
)