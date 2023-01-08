package commandhandlers

import Session
import UserIdentifier
import commands.CommandHandler
import commands.CreateSessionCommand
import java.lang.IllegalStateException
import repositories.QuestionRepository
import repositories.SessionRepository

class CreateSessionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val questionRepository: QuestionRepository
) : CommandHandler<CreateSessionCommand> {
    override fun handle(command: CreateSessionCommand) {
        checkIfThereIsASessionTodayFor(command.userIdentifier)

        val questionOfTheDay =
            questionRepository.getQuestionOfTheDay()
                ?: throw IllegalStateException("Question of the day not found")

        val session = Session(userIdentifier = command.userIdentifier, question = questionOfTheDay)

        sessionRepository.save(session)
    }

    private fun checkIfThereIsASessionTodayFor(userIdentifier: UserIdentifier) {
        sessionRepository.findTodaySessionForUser(userIdentifier)?.let {
            throw RuntimeException("User already has session for today")
        }
    }
}
