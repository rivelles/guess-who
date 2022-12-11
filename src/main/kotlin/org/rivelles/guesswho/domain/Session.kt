package org.rivelles.guesswho.domain

data class Session private constructor(
    val userIdentifier: UserIdentifier,
    val sessionStartedDate: SessionDate,
    var sessionFinishedDate: SessionDate?,
    val question: Question) {

    constructor(userIdentifier: UserIdentifier, question: Question) :
            this(userIdentifier, SessionDate(), null, question)

    fun answerQuestion(questionAnswer: QuestionAnswer) {
        if (question.answer(questionAnswer)) sessionFinishedDate = SessionDate()
    }

    fun isFinished() : Boolean = sessionFinishedDate != null
}
