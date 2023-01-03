package org.rivelles.guesswho.application.commandhandlers

import org.rivelles.guesswho.domain.Question
import org.rivelles.guesswho.domain.QuestionId
import org.rivelles.guesswho.domain.commands.CommandHandler
import org.rivelles.guesswho.domain.commands.CreateQuestionCommand
import org.rivelles.guesswho.domain.repositories.QuestionRepository

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
