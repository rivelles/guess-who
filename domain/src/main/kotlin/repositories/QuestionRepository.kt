package repositories

import Question

interface QuestionRepository {
    fun getQuestionOfTheDay(): Question?
    fun save(question: Question)
}
