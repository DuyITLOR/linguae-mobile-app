package com.penguin.linguae.data.model

import com.google.gson.annotations.SerializedName

data class ClozeQuestion(
    @SerializedName("id") val questionID: Int = -1,
    @SerializedName("sentence") val question: String = "",
)