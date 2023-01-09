package org.rivelles.adapters.http

import UserIdentifier
import commandhandlers.CreateSessionCommandHandler
import commands.CreateSessionCommand
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class SessionsHandler(val createSessionCommandHandler: CreateSessionCommandHandler) {

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
}
