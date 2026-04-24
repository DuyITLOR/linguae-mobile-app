package com.penguin.linguae.data.model

enum class ToeicLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
}

data class ToeicPart5Draft(
    val id: String? = null,
    val question: String = "",
    val options: List<String> = listOf("", "", "", ""),
    val answerIndex: Int = 0,
    val displayOrder: Int = 0,
)

data class ToeicPart6QuestionDraft(
    val id: String? = null,
    val title: Int = 1,
    val question: String = "",
    val options: List<String> = listOf("", "", "", ""),
    val answerIndex: Int = 0,
    val displayOrder: Int = 0,
)

data class ToeicPart6PassageDraft(
    val id: String? = null,
    val passage: String = "",
    val questions: List<ToeicPart6QuestionDraft> = emptyList(),
)

data class CreateToeicEditorRequest(
    val title: String,
    val level: ToeicLevel,
    val part5Questions: List<ToeicPart5Draft>,
    val part6Passages: List<ToeicPart6PassageDraft>,
)

data class UpdateToeicEditorRequest(
    val title: String,
    val level: ToeicLevel,
    val part5Questions: List<ToeicPart5Draft>,
    val part6Passages: List<ToeicPart6PassageDraft>,
)

data class ToeicEditorDetail(
    val id: String,
    val title: String,
    val level: String,
    val readingPart5Questions: List<ReadingPart5Question>,
    val readingPart6Questions: List<ReadingPart6Question>,
)
