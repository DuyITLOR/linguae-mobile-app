package com.penguin.linguae.feature.practice.cloze.viewmodel

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.penguin.linguae.core.navigation.Screen
    import com.penguin.linguae.data.model.ClozeQuestionWithOptions
    import com.penguin.linguae.data.model.ResultData
    import com.penguin.linguae.data.repository.ClozeRepository
    import com.penguin.linguae.data.repository.ResultRepository
    import kotlinx.coroutines.flow.MutableSharedFlow
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.asSharedFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch
    import kotlin.properties.Delegates


    class ClozeViewModelFactory(private val topicId: String) : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ClozeViewModel(topicId) as T
        }
    }

    class ClozeViewModel (
        val topicId: String
    ) : ViewModel() {
        private var totalQuestion by Delegates.notNull<Int>()
        private var correctCount = 0
        private val _repository = ClozeRepository()
        private val _resultRepository = ResultRepository

        private val _selectedOptionId = MutableStateFlow<Int?>(null)
        val selectedOptionId = _selectedOptionId.asStateFlow()

        private val _isAnswered = MutableStateFlow(false)
        val isAnswered = _isAnswered.asStateFlow()
        private val _shuffledQuestionsWithOptions = MutableStateFlow<List<ClozeQuestionWithOptions>>(emptyList())
        val shuffleQuestionsWithOptions = _shuffledQuestionsWithOptions.asStateFlow()

        private val _currentIndex = MutableStateFlow(0)
        val currentIndex = _currentIndex.asStateFlow()

        private val _isLastQuestion = MutableStateFlow(false)
        val isLastQuestion = _isLastQuestion.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading = _isLoading.asStateFlow()

        private val _error = MutableStateFlow<String?>(null)
        val error = _error.asStateFlow()

        private val _navigationEvent = MutableSharedFlow<String>()
        val navigationEvent = _navigationEvent.asSharedFlow()


        init {
            fetchQuestionsWithOptionsByTopic(topicId)
        }

        fun onOptionSelected(optionId: Int) {
            if (_isAnswered.value) return
            if (_shuffledQuestionsWithOptions.value[_currentIndex.value].options[optionId - 1].isCorrect == true)
                correctCount += 1
            _selectedOptionId.value = optionId
            _isAnswered.value = true
        }

        fun onNextQuestionClicked() {
            if (!_isAnswered.value) return

            if (_currentIndex.value >= _shuffledQuestionsWithOptions.value.lastIndex - 1)
                _isLastQuestion.value = true

            _currentIndex.value++
            _selectedOptionId.value = null
            _isAnswered.value = false
        }

        fun fetchQuestionsWithOptionsByTopic(topicId: String) {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    val questions = _repository.getClozeQuestionWithTopicId(topicId)
                    val ids = questions.map { it.questionID }
                    val options = _repository.getClozeOptionsForQuestions(ids)
                    val groupedOptions = options.groupBy { it.questionId }
                    val data = questions.map { question ->
                        ClozeQuestionWithOptions(
                            question = question,
                            options = (groupedOptions[question.questionID] ?: emptyList()).shuffled()
                        )
                    }.shuffled()
                    _shuffledQuestionsWithOptions.value = data
                    _isLastQuestion.value = data.size == 1
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _isLoading.value = false  // always runs whether success or error
                    totalQuestion = _shuffledQuestionsWithOptions.value.size
                }
            }
        }

        fun onResultClicked(){
            val result = ResultData.ClozeResult(
                total = totalQuestion,
                score = correctCount,
                topicId = topicId
            )

            _resultRepository.saveResult(result)
            viewModelScope.launch {
                _navigationEvent.emit(Screen.ResultScreen.route)
            }
        }
    }