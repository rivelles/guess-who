package org.rivelles.commandhandlers

import commands.CommandHandler
import commands.RequestAnotherTipCommand
import org.springframework.stereotype.Component
import repositories.SessionRepository

@Component
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
