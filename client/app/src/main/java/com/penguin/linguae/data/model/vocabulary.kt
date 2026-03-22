package com.penguin.linguae.data.model

data class Column(
    val id: Int,
    val word: String,
    val pronunciation: String,
    val meaning: String,
    val isFavorite: Boolean
)

data class Vocabulary(
    val id: Int,
    val word: String,
    val pronunciation: String,
    val meaning: String,
    val isFavorite: Boolean = false,
    val exampleSentence: String = ""
)