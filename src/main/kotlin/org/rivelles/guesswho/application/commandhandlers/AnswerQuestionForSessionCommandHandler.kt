package org.rivelles.guesswho.application.commandhandlers

import org.rivelles.guesswho.domain.Session
import org.rivelles.guesswho.domain.commands.AnswerQuestionForSessionCommand
import org.rivelles.guesswho.domain.commands.CommandHandler
import org.rivelles.guesswho.domain.repositories.SessionRepository

class AnswerQuestionForSessionCommandHandler(private val sessionRepository: SessionRepository) :
    CommandHandler<AnswerQuestionForSessionCommand> {

    override fun handle(command: AnswerQuestionForSessionCommand) {
        val session =
            sessionRepository.findTodaySessionForUser(command.userIdentifier)
                ?: throw RuntimeException("Session not found for user")

        session.answerQuestion(command.providedAnswer)

        session.takeIf(Session::isFinished)?.let { sessionRepository.save(it) }
    }
}
