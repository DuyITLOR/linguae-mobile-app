package com.penguin.linguae.feature.admin.toeic.editor

import com.penguin.linguae.data.model.ToeicLevel
import com.penguin.linguae.data.model.ToeicPart5Draft
import com.penguin.linguae.data.model.ToeicPart6PassageDraft

data class ToeicEditorState(
    val toeicId: String? = null,
    val title: String = "",
    val level: ToeicLevel = ToeicLevel.BEGINNER,
    val part5Questions: List<ToeicPart5Draft> = emptyList(),
    val part6Passages: List<ToeicPart6PassageDraft> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
) {
    val isEditMode: Boolean
        get() = !toeicId.isNullOrBlank()
}
