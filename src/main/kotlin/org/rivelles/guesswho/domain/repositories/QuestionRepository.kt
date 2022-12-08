package org.rivelles.guesswho.domain.repositories

import org.rivelles.guesswho.domain.Question

interface QuestionRepository {
    fun getQuestionOfTheDay(): Question?
}
