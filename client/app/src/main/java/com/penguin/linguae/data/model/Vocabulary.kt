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
    val VocabularyExample: List<Example>,
    val createdAt: String,
    val updatedAt: String
)

data class Example(
    val id: String,
    val vocabularyId: String,
    val sentence: String,
    val translation: String?,
    val audioUrl: String?,
    val createdAt: String,
)