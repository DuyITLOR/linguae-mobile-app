package com.penguin.linguae.data.model

data class Vocabulary(
    val id: String,
    val topicId: String,
    val word: String,
    val meaning: String,
    val pronunciationText: String,
    val pronunciationAudio: String?,
    val partOfSpeech: String?,
    val difficulty: Int,
    val createdAt: String,
    val updatedAt: String
)