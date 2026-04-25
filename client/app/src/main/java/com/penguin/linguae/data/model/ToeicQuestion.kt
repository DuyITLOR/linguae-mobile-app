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

data class CreateReadingPart5QuestionRequest(
    val toeicId: String,
    val question: String,
    val options: List<String>,
    val answer: Int
)

data class CreateReadingPart6QuestionRequest(
    val toeicId: String,
    val question: String,
    val options: List<CreateReadingPart6OptionRequest>
)

data class CreateReadingPart6OptionRequest(
    val title: Int,
    val options: List<String>,
    val answer: Int
)

data class CreateToeicRequest(
    val title: String,
    val level: String,
    val part5: List<CreateToeicPart5Request>,
    val part6: List<CreateToeicPart6Request>
)

data class CreateToeicPart5Request(
    val question: String,
    val options: List<String>,
    val answer: Int
)

data class CreateToeicPart6Request(
    val question: String,
    val options: List<CreateReadingPart6OptionRequest>
)

data class CreateToeicResponse(
    val message: String,
    val id: String
)

data class SubmitToeicAnswerRequest(
    val toeicId: String,
    val correctAnswer: Int,
    val answer: List<ToeicAnswerRequest>
)

data class ToeicAnswerRequest(
    val questionId: String,
    val selected: Int,
    val part: Int
)

data class SubmitToeicAnswerResponse(
    val message: String
)
