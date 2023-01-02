package org.rivelles.guesswho.domain

import java.util.UUID

data class Question(
    val questionId: QuestionId,
    val questionDescription: QuestionDescription,
    val questionAnswer: QuestionAnswer,
    val questionTips: QuestionTips,
    val questionImage: QuestionImage
) {
    fun answer(providedAnswer: QuestionAnswer): Boolean {
        if (this.questionAnswer.answer == providedAnswer.answer) return true

        return false
    }
}

data class QuestionTips(val tips: List<String>) {
    fun size() = tips.size
}

data class QuestionAnswer(val answer: String)

data class QuestionDescription(val description: String)

data class QuestionId(val id: UUID)

data class QuestionImage(val imageUrl: String)
