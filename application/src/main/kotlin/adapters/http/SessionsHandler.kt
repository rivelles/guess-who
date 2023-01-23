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

@Component
class SessionsHandler(
    private val createSessionCommandHandler: CreateSessionCommandHandler,
    private val answerQuestionForSessionCommandHandler: AnswerQuestionForSessionCommandHandler
) {

    suspend fun save(serverRequest: ServerRequest): ServerResponse {
        val createSessionForUserRequest =
            serverRequest.awaitBodyOrNull(CreateSessionForUserRequest::class)

        return createSessionForUserRequest?.let { request ->
            ServerResponse.ok()
                .bodyValueAndAwait(
                    createSessionCommandHandler.handle(
                        CreateSessionCommand(UserIdentifier(request.userIdentifier))))
        }
            ?: ServerResponse.badRequest().buildAndAwait()
    }

    suspend fun answer(serverRequest: ServerRequest): ServerResponse {
        val userIdentifier =
            serverRequest.pathVariable("userIdentifier").ifEmpty {
                return ServerResponse.badRequest().buildAndAwait()
            }
        val createSessionForUserRequest =
            serverRequest.awaitBodyOrNull(AnswerQuestionForSessionRequest::class)

        return createSessionForUserRequest?.let { request ->
            ServerResponse.ok()
                .bodyValueAndAwait(
                    answerQuestionForSessionCommandHandler.handle(
                        AnswerQuestionForSessionCommand(
                            UserIdentifier(userIdentifier!!),
                            QuestionAnswer(request.providedAnswer))))
        }
            ?: ServerResponse.badRequest().buildAndAwait()
    }
}
