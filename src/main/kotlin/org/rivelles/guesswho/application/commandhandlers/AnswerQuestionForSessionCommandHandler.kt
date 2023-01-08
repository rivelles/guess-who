package org.rivelles.guesswho.application.commandhandlers

import org.rivelles.guesswho.domain.commands.AnswerQuestionForSessionCommand
import org.rivelles.guesswho.domain.commands.CommandHandler
import org.rivelles.guesswho.domain.repositories.SessionRepository

class AnswerQuestionForSessionCommandHandler(private val sessionRepository: SessionRepository) :
    CommandHandler<AnswerQuestionForSessionCommand> {

    override fun handle(command: AnswerQuestionForSessionCommand) {
        sessionRepository.findTodaySessionForUser(command.userIdentifier)?.let { session ->
            session.answerQuestion(command.providedAnswer)

            sessionRepository.takeIf { session.isFinished() }?.save(session)
            session
        }
            ?: throw RuntimeException("Session not found for user")
    }
}
