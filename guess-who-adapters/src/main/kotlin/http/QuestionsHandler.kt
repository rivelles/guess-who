package org.rivelles.http

import QuestionAnswer
import QuestionDateOfAppearance
import QuestionDescription
import QuestionImage
import QuestionTips
import commands.CreateQuestionCommand
import org.rivelles.adapters.persistence.QuestionRepository
import org.rivelles.commandhandlers.CreateQuestionCommandHandler
import org.rivelles.http.requests.CreateQuestionRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

@Component
class QuestionsHandler(questionRepository: QuestionRepository) {
    val createQuestionCommandHandler = CreateQuestionCommandHandler(questionRepository)
    fun save(serverRequest: ServerRequest): Mono<ServerResponse> =
        serverRequest
            .bodyToMono(CreateQuestionRequest::class.java)
            .switchIfEmpty(Mono.error(ServerWebInputException("Bad request.")))
            .flatMap {
                val command =
                    CreateQuestionCommand(
                        QuestionDescription(it.description),
                        QuestionAnswer(it.answer),
                        QuestionTips(it.tips),
                        QuestionImage(it.image),
                        QuestionDateOfAppearance(it.dateAppearance))

                ServerResponse.ok().body(createQuestionCommandHandler.handle(command))
            }
}
