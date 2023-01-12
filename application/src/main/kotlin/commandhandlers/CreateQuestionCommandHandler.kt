package commandhandlers

import Question
import QuestionId
import commands.CommandHandler
import commands.CreateQuestionCommand
import org.springframework.stereotype.Component
import repositories.QuestionRepository

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
