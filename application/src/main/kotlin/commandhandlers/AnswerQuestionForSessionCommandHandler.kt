package org.rivelles.commandhandlers

import commands.AnswerQuestionForSessionCommand
import commands.CommandHandler
import org.springframework.stereotype.Component
import repositories.SessionRepository

@Component
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
