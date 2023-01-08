package commandhandlers

import Question
import QuestionId
import commands.CommandHandler
import commands.CreateQuestionCommand
import repositories.QuestionRepository

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
