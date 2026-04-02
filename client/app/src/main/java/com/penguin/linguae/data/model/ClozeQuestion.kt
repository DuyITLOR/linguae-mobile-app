package com.penguin.linguae.data.model

data class ClozeQuestion(
    val questionNo: Int = 0,
    val sentence: String = "",
    val keyword: String = "",
    val wrongWord: List<String>
)
