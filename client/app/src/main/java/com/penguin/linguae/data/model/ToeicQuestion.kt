package com.penguin.linguae.data.model

data class ReadingPart5Question(
    val id: String,
    val toeicId: String,
    val question: String,
    val options: List<String>,
    val answer: Int
)

data class ReadingPart6Option(
    val id: String = "",
    val title: Int,
    val questionId: String = "",
    val option: List<String>,
    val answer: Int
)

data class ReadingPart6Question(
    val id: String,
    val toeicId: String,
    val question: String,
    val readingPart6Options: List<ReadingPart6Option>
)

data class SubmitToeicAnswerRequest(
    val toeicId: String,
    val answers: List<ToeicAnswerRequest>,
    val correctAnswers: Int
)

data class ToeicAnswerRequest(
    val questionId: String,
    val selected: Int,
    val part: Int
)

data class SubmitToeicAnswerResponse(
    val message: String
)
