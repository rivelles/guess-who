package org.rivelles.commandhandlers

import Question
import QuestionId
import commands.CreateQuestionCommand
import org.rivelles.adapters.persistence.QuestionRepository
import reactor.core.publisher.Mono

class CreateQuestionCommandHandler(private val questionRepository: QuestionRepository) :
    CommandHandler<CreateQuestionCommand> {
    override fun handle(command: CreateQuestionCommand): Mono<Int> {
        return Question(
                QuestionId(),
                command.questionDescription,
                command.questionAnswer,
                command.questionTips,
                command.questionImage,
                command.dateOfAppearance)
            .let { questionRepository.save(it) }
    }
}
