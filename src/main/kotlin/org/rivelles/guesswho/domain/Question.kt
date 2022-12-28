package org.rivelles.guesswho.domain

import java.util.UUID

data class Question(
    val questionId: QuestionId,
    val questionDescription: QuestionDescription,
    val questionAnswer: QuestionAnswer,
    val questionTips: QuestionTips
) {
    fun answer(providedAnswer: QuestionAnswer): Boolean {
        if (this.questionAnswer.answer == providedAnswer.answer) return true

        return false
    }
}

data class QuestionTips(val tips: List<String>)

data class QuestionAnswer(val answer: String)

data class QuestionDescription(val description: String)

data class QuestionId(val id: UUID)
