// data/VocabularyRepository.kt
package com.penguin.linguae.data

import com.penguin.linguae.data.model.Vocabulary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VocabularyRepository {

    private val fakeVocabularyList = listOf(
        Vocabulary(
            id = 1,
            word = "Apple",
            pronunciation = "/'æpəl/",
            meaning = "Quả táo",
            isFavorite = true,
            exampleSentence = "An apple a day keeps the doctor away."
        ),
        Vocabulary(
            id = 2,
            word = "Banana",
            pronunciation = "/bə'nɑ:nə/",
            meaning = "Quả chuối",
            isFavorite = false,
            exampleSentence = "Monkeys love eating bananas."
        ),
        Vocabulary(
            id = 3,
            word = "Coffee",
            pronunciation = "/'kɒfi/",
            meaning = "Cà phê",
            isFavorite = true,
            exampleSentence = "I need a cup of coffee every morning."
        ),
        Vocabulary(
            id = 4,
            word = "Rice",
            pronunciation = "/raɪs/",
            meaning = "Cơm / Gạo",
            isFavorite = false,
            exampleSentence = "Rice is a staple food in Vietnam."
        ),
        Vocabulary(
            id = 5,
            word = "Noodle",
            pronunciation = "/'nu:dl/",
            meaning = "Mì / Phở",
            isFavorite = false,
            exampleSentence = "Pho is a famous Vietnamese noodle soup."
        ),
        Vocabulary(
            id = 6,
            word = "Water",
            pronunciation = "/'wɔ:tər/",
            meaning = "Nước",
            isFavorite = false,
            exampleSentence = "Drink plenty of water every day."
        )
    )

    /**
     * Exposes the fake data as a Flow so the ViewModel can collect it reactively.
     */
    fun getVocabularyList(): Flow<List<Vocabulary>> = flow {
        emit(fakeVocabularyList)
    }
}