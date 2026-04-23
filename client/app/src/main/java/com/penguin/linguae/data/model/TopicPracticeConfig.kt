package com.penguin.linguae.data.model

data class TopicPracticeConfig(
    val id: String,
    val topicId: String,
    val questionType: List<String>,
    val createdAt: String,
    val Topic: Topic? = null
)

data class CreateTopicPracticeConfigRequest(
    val topicId: String,
    val questionType: List<String>
)
