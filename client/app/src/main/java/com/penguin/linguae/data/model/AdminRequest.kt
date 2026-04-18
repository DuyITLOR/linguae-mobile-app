package com.penguin.linguae.data.model

data class CreateTopicRequest(
    val title: String,
    val description: String?,
    val icon: String?,
    val level: String?,
    val displayOrder: Int
)

data class CreateVocabularyExampleRequest(
    val sentence: String,
    val translation: String?
)

data class CreateVocabularyRequest(
    val topicId: String,
    val word: String,
    val meaning: String,
    val pronunciationText: String?,
    val partOfSpeech: String?,
    val difficulty: Int,
    val examples: List<CreateVocabularyExampleRequest>
)
