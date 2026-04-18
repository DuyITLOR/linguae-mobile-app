package com.penguin.linguae.data.model

data class Topic(
    val id: String,
    val title: String,
    val description: String? = null,
    val icon: String? = null,
    val level: String,
    val displayOrder: Int = 0,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String,
    val _count: Count
)

data class TopicIncludeVocab(
    val id: String,
    val title: String,
    val description: String? = null,
    val icon: String? = null,
    val level: String,
    val displayOrder: Int = 0,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String,
    val Vocabulary: List<Vocabulary>
)

data class Count(
    val Vocabulary: Int
)