package org.rivelles.adapters.postgresql

import Question
import org.springframework.stereotype.Component
import repositories.QuestionRepository

@Component
class QuestionR2DBCRepository : QuestionRepository {
    override fun getQuestionOfTheDay(): Question? {
        TODO("Not yet implemented")
    }

    override fun save(question: Question) {
        TODO("Not yet implemented")
    }
}
