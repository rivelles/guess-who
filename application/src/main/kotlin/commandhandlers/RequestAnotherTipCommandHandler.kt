package org.rivelles.commandhandlers

import commands.RequestAnotherTipCommand
import org.rivelles.adapters.persistence.SessionRepository
import reactor.core.publisher.Mono

class RequestAnotherTipCommandHandler(private val sessionRepository: SessionRepository) :
    CommandHandler<RequestAnotherTipCommand> {
    override fun handle(command: RequestAnotherTipCommand): Mono<Int> {
        return sessionRepository
            .findTodaySessionForUser(command.userIdentifier)
            .switchIfEmpty(Mono.error(RuntimeException("Session not found for user")))
            .flatMap { session ->
                session!!.requestOneMoreTip()
                sessionRepository.save(session)
            }
    }
}
