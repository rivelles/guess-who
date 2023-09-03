package org.rivelles.http

import QuestionAnswer
import UserIdentifier
import commands.AnswerQuestionForSessionCommand
import commands.CreateSessionCommand
import org.rivelles.adapters.persistence.QuestionRepository
import org.rivelles.adapters.persistence.SessionRepository
import org.rivelles.commandhandlers.AnswerQuestionForSessionCommandHandler
import org.rivelles.commandhandlers.CreateSessionCommandHandler
import org.rivelles.http.requests.AnswerQuestionForSessionRequest
import org.rivelles.http.requests.CreateSessionForUserRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ServerWebInputException
import queries.FindTodaySessionForUser
import queryhandlers.FindTodaySessionForUserQueryHandler
import reactor.core.publisher.Mono

@Component
class SessionsHandler(
    sessionRepository: SessionRepository,
    questionRepository: QuestionRepository
) {
    val createSessionCommandHandler =
        CreateSessionCommandHandler(sessionRepository, questionRepository)
    val answerQuestionForSessionCommandHandler =
        AnswerQuestionForSessionCommandHandler(sessionRepository)
    val findTodaySessionForUserQueryHandler = FindTodaySessionForUserQueryHandler(sessionRepository)

    fun save(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest
            .bodyToMono(CreateSessionForUserRequest::class.java)
            .switchIfEmpty(Mono.error(ServerWebInputException("Bad request.")))
            .flatMap {
                ServerResponse.ok().body(createSessionCommandHandler.handle(it.toCommand()))
            }

    fun answer(serverRequest: ServerRequest): Mono<ServerResponse> {
        val userIdentifier =
            serverRequest.pathVariable("userIdentifier").ifEmpty {
                return ServerResponse.badRequest().build()
            }

        return serverRequest.bodyToMono(AnswerQuestionForSessionRequest::class.java).flatMap {
            ServerResponse.ok()
                .body(answerQuestionForSessionCommandHandler.handle(it.toCommand(userIdentifier)))
        }
    }

    fun getByUserIdentifier(serverRequest: ServerRequest): Mono<ServerResponse> {
        val userIdentifier =
            serverRequest.pathVariable("userIdentifier").ifEmpty {
                return ServerResponse.badRequest().build()
            }

        return findTodaySessionForUserQueryHandler.handle(
                FindTodaySessionForUser(UserIdentifier(userIdentifier)))
            .flatMap { session ->
                session?.let { ServerResponse.ok().bodyValue(it) }
                    ?: ServerResponse.notFound().build()
            }
    }
}

private fun CreateSessionForUserRequest.toCommand() =
    CreateSessionCommand(UserIdentifier(this.userIdentifier))

private fun AnswerQuestionForSessionRequest.toCommand(userIdentifier: String) =
    AnswerQuestionForSessionCommand(
        UserIdentifier(userIdentifier), QuestionAnswer(this.providedAnswer))
