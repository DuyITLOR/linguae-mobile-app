package com.penguin.linguae.data.model

enum class FlashcardReviewStatus {
	LEARNING,
	MASTERED
}

data class FlashcardReviewRequest(
	val vocabularyId: String,
	val status: FlashcardReviewStatus
)

data class FlashcardTopic(
	val id: String,
	val title: String,
	val level: String,
	val learnedWords: Int,
	val totalWords: Int
)

