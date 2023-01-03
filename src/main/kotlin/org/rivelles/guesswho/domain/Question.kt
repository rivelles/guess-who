package org.rivelles.guesswho.domain

import java.time.LocalDate
import java.util.UUID
import org.valiktor.functions.isWebsite
import org.valiktor.validate

data class Question(
    val questionId: QuestionId,
    val questionDescription: QuestionDescription,
    val questionAnswer: QuestionAnswer,
    val questionTips: QuestionTips,
    val questionImage: QuestionImage,
    val questionDateOfAppearance: QuestionDateOfAppearance
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

data class QuestionId(val id: UUID) {
    constructor() : this(UUID.randomUUID())
}

data class QuestionImage(val imageUrl: String) {
    init {
        validate(this) { validate(QuestionImage::imageUrl).isWebsite() }
    }
}

data class QuestionDateOfAppearance(val dateOfAppearance: LocalDate)
