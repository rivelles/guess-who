package org.rivelles.guesswho.domain

data class Session
private constructor(
    val userIdentifier: UserIdentifier,
    val sessionStartedDate: SessionDate,
    var sessionFinishedDate: SessionDate?,
    val question: Question,
    var showedTips: QuestionTips
) {

    constructor(
        userIdentifier: UserIdentifier,
        question: Question
    ) : this(userIdentifier, SessionDate(), null, question, QuestionTips(emptyList()))

    fun answerQuestion(questionAnswer: QuestionAnswer) {
        if (question.answer(questionAnswer)) sessionFinishedDate = SessionDate()
    }

    fun isFinished(): Boolean = sessionFinishedDate != null

    fun requestOneMoreTip() {
        showedTips
            .let { tips ->
                IllegalStateException("All tips are already shown.")
                    .takeIf { tips.size() == question.questionTips.size() }
                    ?.run { throw this }

                tips.tips
                    .toMutableList()
                    .let {
                        it.add(question.questionTips.tips[tips.size()])
                        it
                    }
                    .toList()
            }
            .apply { showedTips = QuestionTips(this) }
    }
}
