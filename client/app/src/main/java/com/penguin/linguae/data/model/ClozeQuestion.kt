package com.penguin.linguae.data.model

import com.google.gson.annotations.SerializedName

data class ClozeQuestion(
    val topicId: String = "",
    @SerializedName("id") val questionID: Int = -1,
    @SerializedName("sentence") val question: String = "",
)

data class CreateClozeOptionRequest(
    val optionText: String,
    val isCorrect: Boolean,
    val blankIndex: Int
)

data class CreateClozeQuestionRequest(
    val topicId: String,
    val sentence: String,
    val options: List<CreateClozeOptionRequest>
)