package org.rivelles.commandhandlers

import commands.AnswerQuestionForSessionCommand
import java.lang.RuntimeException
import org.rivelles.adapters.persistence.SessionRepository
import reactor.core.publisher.Mono

class AnswerQuestionForSessionCommandHandler(private val sessionRepository: SessionRepository) :
    CommandHandler<AnswerQuestionForSessionCommand> {

    override fun handle(command: AnswerQuestionForSessionCommand): Mono<Int> {
        return sessionRepository
            .findTodaySessionForUser(command.userIdentifier)
            .switchIfEmpty(
                Mono.error(RuntimeException("Couldn't find a session for the user today")))
            .flatMap {
                it!!.answerQuestion(command.providedAnswer)

                if (it.isFinished()) sessionRepository.save(it) else Mono.empty()
            }
    }
}
