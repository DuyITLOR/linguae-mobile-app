package com.penguin.linguae.feature.admin.toeic.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.penguin.linguae.data.model.CreateReadingPart6OptionRequest
import com.penguin.linguae.data.model.CreateToeicPart5Request
import com.penguin.linguae.data.model.CreateToeicPart6Request
import com.penguin.linguae.data.model.CreateToeicRequest
import com.penguin.linguae.data.repository.ToeicRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class ExamInfoDraft(
    val title: String = "",
    val level: String = "BEGINNER"
)

data class Part5QuestionDraft(
    val id: String = System.currentTimeMillis().toString(),
    val sentence: String = "",
    val options: List<String> = listOf("", "", "", ""),
    val answer: Int = 0
)

data class Part6QuestionDraft(
    val id: String = System.currentTimeMillis().toString(),
    val title: Int = 1,
    val options: List<String> = listOf("", "", "", ""),
    val answer: Int = 0
)

data class Part6PassageDraft(
    val id: String = System.currentTimeMillis().toString(),
    val passage: String = "",
    val questions: List<Part6QuestionDraft> = emptyList()
)

class ToeicEditorViewModel : ViewModel() {

    private val toeicRepository = ToeicRepository()

    var examInfo by mutableStateOf(ExamInfoDraft())
        private set

    var isLoadingDraft by mutableStateOf(false)
        private set

    var draftLoadError by mutableStateOf<String?>(null)
        private set

    private var activeToeicId: String? = null
    private var createDraftStarted = false

    fun updateExamInfo(updated: ExamInfoDraft) {
        examInfo = updated
    }

    val part5Questions = mutableStateListOf<Part5QuestionDraft>()
    val part6Passages = mutableStateListOf<Part6PassageDraft>()

    fun addPart5Question() {
        part5Questions.add(Part5QuestionDraft(id = System.currentTimeMillis().toString()))
    }

    fun updatePart5Question(id: String, updated: Part5QuestionDraft) {
        for (i in part5Questions.indices) {
            if (part5Questions[i].id == id) {
                part5Questions[i] = updated
                break
            }
        }
    }

    fun removePart5Question(id: String) {
        for (i in part5Questions.indices) {
            if (part5Questions[i].id == id) {
                part5Questions.removeAt(i)
                break
            }
        }
    }

    fun addPart6Passage() {
        part6Passages.add(Part6PassageDraft(id = System.currentTimeMillis().toString()))
    }

    fun updatePart6Passage(id: String, updated: Part6PassageDraft) {
        val i = part6Passages.indexOfFirst { it.id == id }
        if (i >= 0) part6Passages[i] = updated
    }

    fun removePart6Passage(id: String) {
        val i = part6Passages.indexOfFirst { it.id == id }
        if (i >= 0) part6Passages.removeAt(i)
    }

    fun addQuestionToPassage(passageId: String) {
        val i = part6Passages.indexOfFirst { it.id == passageId }
        if (i >= 0) {
            val passage = part6Passages[i]
            val nextTitle = (passage.questions.maxOfOrNull { it.title } ?: 0) + 1
            val newQuestion = Part6QuestionDraft(
                id = System.currentTimeMillis().toString(),
                title = nextTitle
            )
            part6Passages[i] = passage.copy(questions = passage.questions + newQuestion)
        }
    }

    fun updateQuestionInPassage(passageId: String, questionId: String, updated: Part6QuestionDraft) {
        val pi = part6Passages.indexOfFirst { it.id == passageId }
        if (pi >= 0) {
            val passage = part6Passages[pi]
            val newQuestions = passage.questions.map { if (it.id == questionId) updated else it }
            part6Passages[pi] = passage.copy(questions = newQuestions)
        }
    }

    fun removeQuestionFromPassage(passageId: String, questionId: String) {
        val pi = part6Passages.indexOfFirst { it.id == passageId }
        if (pi >= 0) {
            val passage = part6Passages[pi]
            part6Passages[pi] = passage.copy(questions = passage.questions.filter { it.id != questionId })
        }
    }

    fun startCreateDraft() {
        if (activeToeicId != null || !createDraftStarted) {
            clearAllDrafts()
            createDraftStarted = true
        }
    }

    suspend fun loadToeicDraft(toeicId: String): String? {
        if (toeicId.isBlank()) return null
        if (activeToeicId == toeicId && draftLoadError == null) return null

        isLoadingDraft = true
        draftLoadError = null

        return try {
            coroutineScope {
                val toeicDeferred = async { toeicRepository.getToeicById(toeicId).getOrThrow() }
                val part5Deferred = async { toeicRepository.getReadingPart5Questions(toeicId).getOrThrow() }
                val part6Deferred = async { toeicRepository.getReadingPart6Questions(toeicId).getOrThrow() }

                val toeic = toeicDeferred.await()
                val part5 = part5Deferred.await()
                val part6 = part6Deferred.await()

                examInfo = ExamInfoDraft(
                    title = toeic.title,
                    level = toeic.level
                )

                part5Questions.clear()
                part5Questions.addAll(
                    part5.map { question ->
                        Part5QuestionDraft(
                            id = question.id,
                            sentence = question.question,
                            options = normalizeOptions(question.options),
                            answer = question.answer
                        )
                    }
                )

                part6Passages.clear()
                part6Passages.addAll(
                    part6.map { passage ->
                        Part6PassageDraft(
                            id = passage.id,
                            passage = normalizePassageForEditor(passage.question),
                            questions = passage.readingPart6Options
                                .sortedBy { option -> option.title }
                                .map { option ->
                                    Part6QuestionDraft(
                                        id = option.id,
                                        title = option.title,
                                        options = normalizeOptions(option.option),
                                        answer = option.answer
                                    )
                                }
                        )
                    }
                )

                activeToeicId = toeicId
                createDraftStarted = false
                null
            }
        } catch (e: Exception) {
            Log.e("ToeicEditorVM", "Load TOEIC draft failed", e)
            val message = e.message ?: "Khong the tai noi dung de TOEIC"
            draftLoadError = message
            message
        } finally {
            isLoadingDraft = false
        }
    }

    suspend fun saveQuestion(): String? {
        validateDrafts()?.let { return it }

        return try {
            val editingToeicId = activeToeicId
            val request = CreateToeicRequest(
                title = examInfo.title.trim(),
                level = examInfo.level,
                part5 = part5Questions.map { question ->
                    CreateToeicPart5Request(
                        question = question.sentence.trim(),
                        options = question.options.map { it.trim() },
                        answer = question.answer
                    )
                },
                part6 = part6Passages.map { passage ->
                    CreateToeicPart6Request(
                        question = passage.passage
                            .trim()
                            .replace("\r\n", "\n")
                            .replace("\r", "\n")
                            .replace("\n", "<br/>"),
                        options = passage.questions.map { question ->
                            CreateReadingPart6OptionRequest(
                                title = question.title,
                                options = question.options.map { it.trim() },
                                answer = question.answer
                            )
                        }
                    )
                }
            )

            if (editingToeicId != null) {
                val deleteResult = toeicRepository.deleteToeic(editingToeicId)
                deleteResult.exceptionOrNull()?.let { error ->
                    Log.e("ToeicEditorVM", "Delete existing TOEIC failed", error)
                    return error.message ?: "Khong the xoa de TOEIC cu"
                }
            }

            val createResult = toeicRepository.createToeic(request)

            createResult.fold(
                onSuccess = { createdToeic ->
                    Log.d("ToeicEditorVM", "Saved TOEIC with questions: ${createdToeic.id}")
                    clearAllDrafts()
                    null
                },
                onFailure = { error ->
                    Log.e("ToeicEditorVM", "Save TOEIC failed", error)
                    error.message ?: "Khong the tao de TOEIC"
                }
            )
        } catch (e: Exception) {
            Log.e("ToeicEditorVM", "Save TOEIC failed", e)
            e.message ?: "Khong the tao de TOEIC"
        }
    }

    private fun validateDrafts(): String? {
        if (examInfo.title.isBlank()) {
            return "Vui long dien tieu de"
        }
        if (part5Questions.isEmpty()) {
            return "Part 5 dang trong. Vui long them it nhat mot cau hoi."
        }
        val invalidPart5Index = part5Questions.indexOfFirst { it.sentence.isBlank() }
        if (invalidPart5Index >= 0) {
            return "Vui long dien noi dung cau hoi Part 5 (cau ${invalidPart5Index + 1})."
        }
        val invalidPart5OptionIndex = part5Questions.indexOfFirst { question ->
            question.options.any { it.isBlank() }
        }
        if (invalidPart5OptionIndex >= 0) {
            return "Vui long dien du 4 dap an Part 5 (cau ${invalidPart5OptionIndex + 1})."
        }

        if (part6Passages.isEmpty()) {
            return "Part 6 dang trong. Vui long them it nhat mot bai doc."
        }
        val invalidPart6PassageIndex = part6Passages.indexOfFirst { it.passage.isBlank() }
        if (invalidPart6PassageIndex >= 0) {
            return "Vui long dien doan van Part 6 (bai doc ${invalidPart6PassageIndex + 1})."
        }

        val invalidPart6Question = part6Passages.withIndex().firstOrNull { (_, passage) ->
            passage.questions.isEmpty() ||
                passage.questions.any { question ->
                    question.title <= 0 || question.options.any { it.isBlank() }
                }
        }
        if (invalidPart6Question != null) {
            return "Vui long dien day du cau hoi Part 6 o bai doc ${invalidPart6Question.index + 1}."
        }

        return null
    }

    fun clearAllDrafts() {
        examInfo = ExamInfoDraft()
        part5Questions.clear()
        part6Passages.clear()
        activeToeicId = null
        createDraftStarted = false
        draftLoadError = null
        isLoadingDraft = false
    }

    private fun normalizeOptions(options: List<String>): List<String> {
        return (options + List(4) { "" }).take(4)
    }

    private fun normalizePassageForEditor(passage: String): String {
        return passage
            .replace("<br/>", "\n", ignoreCase = true)
            .replace("<br>", "\n", ignoreCase = true)
    }
}
