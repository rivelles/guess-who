package org.rivelles.commandhandlers

import Session
import commands.CreateSessionCommand
import org.rivelles.adapters.persistence.QuestionRepository
import org.rivelles.adapters.persistence.SessionRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CreateSessionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val questionRepository: QuestionRepository
) : CommandHandler<CreateSessionCommand> {
    override fun handle(command: CreateSessionCommand): Mono<Int> {
        return sessionRepository
            .findTodaySessionForUser(command.userIdentifier)
            .doOnEach {
                if (it.hasValue()) throw RuntimeException("User already has session for today")
            }
            .then(
                questionRepository
                    .getQuestionOfTheDay()
                    .switchIfEmpty(
                        Mono.error(IllegalStateException("Question of the day not found")))
                    .flatMap {
                        val session =
                            Session(userIdentifier = command.userIdentifier, question = it!!)
                        sessionRepository.save(session)
                    })
    }
}
