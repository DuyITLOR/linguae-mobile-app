package com.penguin.linguae.feature.admin.toeic.editor

object ToeicEditorNavigation {
    const val CREATE_ROUTE = "admin/toeic/editor/create"
    const val EDIT_ROUTE = "admin/toeic/editor/{toeicId}"

    fun editRoute(toeicId: String): String = "admin/toeic/editor/$toeicId"
}
