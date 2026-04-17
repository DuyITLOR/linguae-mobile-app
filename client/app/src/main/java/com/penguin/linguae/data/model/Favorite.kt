package com.penguin.linguae.data.model

data class Favorite(
    val userId: String,
    val vocabularyId: String,
    val note: String?,
    val createdAt: String,
    val Vocabulary: Vocabulary
)