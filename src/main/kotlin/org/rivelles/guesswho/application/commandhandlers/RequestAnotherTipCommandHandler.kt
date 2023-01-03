package org.rivelles.guesswho.application.commandhandlers

import org.rivelles.guesswho.domain.commands.CommandHandler
import org.rivelles.guesswho.domain.commands.RequestAnotherTipCommand
import org.rivelles.guesswho.domain.repositories.SessionRepository

class RequestAnotherTipCommandHandler(private val sessionRepository: SessionRepository) :
    CommandHandler<RequestAnotherTipCommand> {
    override fun handle(command: RequestAnotherTipCommand) {
        sessionRepository
            .findTodaySessionForUser(command.userIdentifier)
            ?.let { session ->
                session.requestOneMoreTip()
                session
            }
            ?.run { sessionRepository.save(this) }
            ?: throw RuntimeException("Session not found for user")
    }
}
