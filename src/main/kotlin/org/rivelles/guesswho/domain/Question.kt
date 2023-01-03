package org.rivelles.guesswho.domain

import java.time.LocalDate
import java.util.UUID
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank
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
    init {
        validate(this) { validate(QuestionTips::tips).hasSize(0, 10) }
    }
    fun size() = tips.size
}

data class QuestionAnswer(val answer: String) {
    init {
        validate(this) { validate(QuestionAnswer::answer).isNotBlank() }
    }
}

data class QuestionDescription(val description: String) {
    init {
        validate(this) { validate(QuestionDescription::description).isNotBlank() }
    }
}

data class QuestionId(val id: UUID) {
    constructor() : this(UUID.randomUUID())
}

data class QuestionImage(val imageUrl: String) {
    init {
        validate(this) { validate(QuestionImage::imageUrl).isWebsite() }
    }
}

data class QuestionDateOfAppearance(val dateOfAppearance: LocalDate)
