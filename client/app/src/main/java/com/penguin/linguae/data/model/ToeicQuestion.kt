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
    val option: List<String>,
    val answer: Int
)

data class ReadingPart6Question(
    val id: String,
    val toeicId: String,
    val question: String,
    val readingPart6Options: List<ReadingPart6Option>
)
