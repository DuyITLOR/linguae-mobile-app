package com.penguin.linguae.data.model

data class ClozeQuestionWithOptions(
    val question: ClozeQuestion,
    val options: List<ClozeOption>
)