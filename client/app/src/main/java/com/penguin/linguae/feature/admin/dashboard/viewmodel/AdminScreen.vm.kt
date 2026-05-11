package com.penguin.linguae.feature.admin.dashboard.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penguin.linguae.data.model.Vocabulary
import com.penguin.linguae.data.repository.ToeicRepository
import com.penguin.linguae.data.repository.TopicRepository
import com.penguin.linguae.data.repository.UserRepository
import com.penguin.linguae.data.repository.VocabularyRepository
import kotlinx.coroutines.launch

class AdminScreenViewModel(
    private val _userRepository: UserRepository = UserRepository(),
    private val _wordRepository: VocabularyRepository = VocabularyRepository(),
    private val _topicRepository: TopicRepository = TopicRepository(),
    private val _examRepository: ToeicRepository = ToeicRepository()
) : ViewModel() {

    private val _numberOfUsers = mutableIntStateOf(0)
    val numberOfUsers = _numberOfUsers

    private val _numberOfWords = mutableIntStateOf(0)
    val numberOfWords = _numberOfWords

    private val _numberOfTopics = mutableIntStateOf(0)
    val numberOfTopics = _numberOfTopics

    private val _numberOfToeicExams = mutableIntStateOf(0)
    val numberOfToeicExams = _numberOfToeicExams

    init {
        getNumberOfUsers()
        getNumberOfWords()
        getNumberOfTopics()
        getNumberOfToeicExams()
    }

    private fun getNumberOfUsers() {

        viewModelScope.launch {

            _userRepository.getNumberOfUsers()
                .onSuccess { number ->

                    _numberOfUsers.intValue = number

                }
                .onFailure { e ->

                    Log.e(
                        "AdminVM",
                        "Error getting users",
                        e
                    )
                }
        }
    }

    private fun getNumberOfWords() {

        viewModelScope.launch {

            _wordRepository.getAllVocabulary("")
                .onSuccess { data ->

                    _numberOfWords.intValue = data.size

                }
                .onFailure { e ->

                    Log.e(
                        "AdminVM",
                        "Error getting vocabulary",
                        e
                    )
                }
        }
    }

    private fun getNumberOfTopics() {

        viewModelScope.launch {

            _topicRepository.getAllTopic("")
                .onSuccess { data ->

                    _numberOfTopics.intValue = data.size

                }
                .onFailure { e ->

                    Log.e(
                        "AdminVM",
                        "Error getting topics",
                        e
                    )
                }
        }
    }

    private fun getNumberOfToeicExams() {

        viewModelScope.launch {

            _examRepository.getAllToeic()
                .onSuccess { data ->

                    _numberOfToeicExams.intValue = data.size

                }
                .onFailure { e ->

                    Log.e(
                        "AdminVM",
                        "Error getting toeic exams",
                        e
                    )
                }
        }
    }
}