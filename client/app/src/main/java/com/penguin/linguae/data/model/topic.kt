package com.penguin.linguae.data.model

data class Topic(
    val id: Int,
    val title: String,
    val wordCount: Int,
    val progressPercent: Int? = null
)