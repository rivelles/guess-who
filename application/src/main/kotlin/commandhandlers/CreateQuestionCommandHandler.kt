package org.rivelles.commandhandlers

import Question
import QuestionId
import commands.CreateQuestionCommand
import org.rivelles.adapters.persistence.QuestionRepository
import org.springframework.stereotype.Component

@Component
class CreateQuestionCommandHandler(private val questionRepository: QuestionRepository) :
    CommandHandler<CreateQuestionCommand> {
    override fun handle(command: CreateQuestionCommand) {
        Question(
                QuestionId(),
                command.questionDescription,
                command.questionAnswer,
                command.questionTips,
                command.questionImage,
                command.dateOfAppearance)
            .let { questionRepository.save(it) }
    }
}
