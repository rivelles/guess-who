package org.rivelles.guesswho.application.commandhandlers

import org.rivelles.guesswho.domain.Session
import org.rivelles.guesswho.domain.commands.CommandHandler
import org.rivelles.guesswho.domain.commands.RequestAnotherTipCommand
import org.rivelles.guesswho.domain.repositories.SessionRepository

class RequestAnotherTipCommandHandler(private val sessionRepository: SessionRepository) :
    CommandHandler<RequestAnotherTipCommand> {
    override fun handle(command: RequestAnotherTipCommand): Session {
        TODO("Not yet implemented")
    }
}
