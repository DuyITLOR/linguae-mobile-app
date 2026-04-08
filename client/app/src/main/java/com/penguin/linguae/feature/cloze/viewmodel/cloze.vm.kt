    package com.penguin.linguae.feature.cloze.viewmodel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import androidx.navigation.NavController
    import com.penguin.linguae.data.model.ClozeQuestionWithOptions
    import com.penguin.linguae.data.repository.ClozeRepository
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch


    class ClozeViewModel (
        val topicId: String
    ) : ViewModel() {
        private val _repository = ClozeRepository()

        private val _selectedOptionId = MutableStateFlow<Int?>(null)
        val selectedOptionId = _selectedOptionId.asStateFlow()

        private val _isAnswered = MutableStateFlow(false)
        val isAnswered = _isAnswered.asStateFlow()

        private val _questionsWithOptions = MutableStateFlow<List<ClozeQuestionWithOptions>>(emptyList())
        val questionsWithOptions = _questionsWithOptions.asStateFlow()

        private val _currentIndex = MutableStateFlow(0)
        val currentIndex = _currentIndex.asStateFlow()

        private val _isFinished = MutableStateFlow(false)
        val isFinished = _isFinished.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading = _isLoading.asStateFlow()

        private val _error = MutableStateFlow<String?>(null)
        val error = _error.asStateFlow()

        init {
            fetchQuestionsWithOptionsByTopic(topicId)
        }

        fun onOptionSelected(optionId: Int) {
            if (_isAnswered.value) return
            _selectedOptionId.value = optionId
            _isAnswered.value = true
        }

        fun onNextQuestionClicked() {
            if (!_isAnswered.value) return

            if (_currentIndex.value >= _questionsWithOptions.value.lastIndex - 1)
                _isFinished.value = true

            _currentIndex.value++
            _selectedOptionId.value = null
            _isAnswered.value = false
        }

        private fun fetchQuestionsWithOptionsByTopic(topicId: String) {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    val questions = _repository.getClozeQuestionWithTopicId(topicId)
                    val ids = questions.map { it.questionID }
                    val options = _repository.getClozeOptionsForQuestions(ids)
                    val groupedOptions = options.groupBy { it.questionId }
                    _questionsWithOptions.value = questions.map { question ->
                        ClozeQuestionWithOptions(
                            question = question,
                            options = groupedOptions[question.questionID] ?: emptyList()
                        )
                    }
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _isLoading.value = false  // always runs whether success or error
                }
            }
        }
    }