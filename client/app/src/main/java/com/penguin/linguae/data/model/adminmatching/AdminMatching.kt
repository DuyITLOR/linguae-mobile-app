package com.penguin.linguae.data.model.adminmatching

import com.google.gson.annotations.SerializedName

enum class MatchingManageMode {
    LIST,
    CREATE,
    EDIT
}

data class MatchingDraftPair(
    val id: Int,
    val leftText: String = "",
    val rightText: String = ""
)

data class MatchingPairUi(
    val id: String,
    val matchingQuestionId: String? = null,
    val leftText: String,
    val rightText: String,
    val displayOrder: Int = 0,
    val createdAt: String? = null
)

data class MatchingQuestionUi(
    val id: String,
    val topicId: String,
    val title: String,
    @SerializedName("MatchingPair")
    val pairs: List<MatchingPairUi> = emptyList(),
    val createdAt: String? = null
)

data class MatchingPairRequest(
    val leftText: String,
    val rightText: String,
    val displayOrder: Int? = null
)

data class CreateMatchingQuestionRequest(
    val topicId: String,
    val title: String,
    val pairs: List<MatchingPairRequest>
)

data class UpdateMatchingQuestionRequest(
    val topicId: String? = null,
    val title: String? = null,
    val pairs: List<MatchingPairRequest>? = null
)
