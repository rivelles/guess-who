package org.rivelles.adapters.http

import QuestionAnswer
import UserIdentifier
import commands.AnswerQuestionForSessionCommand
import commands.CreateSessionCommand
import org.rivelles.adapters.http.requests.AnswerQuestionForSessionRequest
import org.rivelles.adapters.http.requests.CreateSessionForUserRequest
import org.rivelles.commandhandlers.AnswerQuestionForSessionCommandHandler
import org.rivelles.commandhandlers.CreateSessionCommandHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

@Component
class SessionsHandler(
    private val createSessionCommandHandler: CreateSessionCommandHandler,
    private val answerQuestionForSessionCommandHandler: AnswerQuestionForSessionCommandHandler
) {

    fun save(serverRequest: ServerRequest): Mono<ServerResponse> {
        val createSessionForUserRequest =
            serverRequest.bodyToMono(CreateSessionForUserRequest::class.java)

        return createSessionForUserRequest.flatMap {
            ServerResponse.ok()
                .body(
                    createSessionCommandHandler.handle(
                        CreateSessionCommand(UserIdentifier(it.userIdentifier))))
        }
    }

    fun answer(serverRequest: ServerRequest): Mono<ServerResponse> {
        val userIdentifier =
            serverRequest.pathVariable("userIdentifier").ifEmpty {
                return ServerResponse.badRequest().build()
            }
        val createSessionForUserRequest =
            serverRequest.bodyToMono(AnswerQuestionForSessionRequest::class.java)

        return createSessionForUserRequest.flatMap {
            ServerResponse.ok()
                .body(
                    answerQuestionForSessionCommandHandler.handle(
                        AnswerQuestionForSessionCommand(
                            UserIdentifier(userIdentifier), QuestionAnswer(it.providedAnswer))))
        }
    }
}
