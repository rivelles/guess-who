package org.rivelles.guesswho.application.commandhandlers

import java.lang.IllegalStateException
import org.rivelles.guesswho.domain.Session
import org.rivelles.guesswho.domain.UserIdentifier
import org.rivelles.guesswho.domain.commands.CommandHandler
import org.rivelles.guesswho.domain.commands.CreateSessionCommand
import org.rivelles.guesswho.domain.repositories.QuestionRepository
import org.rivelles.guesswho.domain.repositories.SessionRepository

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
