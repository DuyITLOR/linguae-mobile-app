package com.penguin.linguae.data.model

data class ClozeOption(
    val questionID: Int = -1,
    val answer: String = "",
    val isCorrect: Boolean? = null,
    val onClick: () -> Unit = {}
)