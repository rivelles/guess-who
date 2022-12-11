package org.rivelles.guesswho.application.commandhandlers

import org.rivelles.guesswho.domain.commands.AnswerQuestionForSessionCommand
import org.rivelles.guesswho.domain.repositories.SessionRepository

class AnswerQuestionForSessionCommandHandler(private val sessionRepository: SessionRepository) {

    fun handle(answerQuestionForSessionCommand: AnswerQuestionForSessionCommand): Boolean {
        val session = sessionRepository.findTodaySessionForUser(answerQuestionForSessionCommand.userIdentifier)
            ?: throw RuntimeException("Session not found for user")

        session.answerQuestion(answerQuestionForSessionCommand.providedAnswer)

        if (session.isFinished()) {
            sessionRepository.save(session)
            return true
        }
        return false
    }
}