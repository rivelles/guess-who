package org.rivelles.commandhandlers

import commands.AnswerQuestionForSessionCommand
import java.lang.RuntimeException
import org.rivelles.adapters.persistence.SessionRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AnswerQuestionForSessionCommandHandler(private val sessionRepository: SessionRepository) :
    CommandHandler<AnswerQuestionForSessionCommand> {

    override fun handle(command: AnswerQuestionForSessionCommand): Mono<Unit> {
        return sessionRepository
            .findTodaySessionForUser(command.userIdentifier)
            ?.switchIfEmpty(Mono.error(RuntimeException("")))
            .map {
                it!!.answerQuestion(command.providedAnswer)

                if (it.isFinished()) sessionRepository.save(it)
            }
    }
}
