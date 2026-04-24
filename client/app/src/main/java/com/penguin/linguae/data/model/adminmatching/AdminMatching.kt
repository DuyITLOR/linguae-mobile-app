package com.penguin.linguae.data.model.adminmatching

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

data class MatchingQuestionUi(
    val id: String,
    val topicId: String,
    val title: String,
    val pairs: List<MatchingDraftPair>
)
