    package com.penguin.linguae.feature.cloze.viewmodel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.penguin.linguae.data.model.ClozeQuestionWithOptions
    import com.penguin.linguae.data.repository.ClozeRepository
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch


    class ClozeViewModel : ViewModel() {
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
            fetchQuestionsWithOptions()
        }

        fun onOptionSelected(optionId: Int) {
            if (_isAnswered.value) return
            _selectedOptionId.value = optionId
            _isAnswered.value = true
        }

        fun onNextQuestionClicked() {
            if (!_isAnswered.value) return

            if (_currentIndex.value < _questionsWithOptions.value.lastIndex) {
                _currentIndex.value++
                _selectedOptionId.value = null
                _isAnswered.value = false
            } else {
                _isFinished.value = true
            }
        }

        private fun fetchQuestionsWithOptions() {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    val questions = _repository.getClozeQuestions()
                    val ids = questions.map { it.questionID }
                    val options = _repository.getClozeOptionsForQuestions(ids)
                    _questionsWithOptions.value = questions.zip(options).map { (question, optionList) ->
                        ClozeQuestionWithOptions(
                            question = question,
                            options = optionList
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