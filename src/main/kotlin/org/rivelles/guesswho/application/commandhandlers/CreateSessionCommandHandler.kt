package org.rivelles.guesswho.application.commandhandlers

import org.rivelles.guesswho.domain.Session
import org.rivelles.guesswho.domain.SessionDate
import org.rivelles.guesswho.domain.UserIdentifier
import org.rivelles.guesswho.domain.commands.CreateSessionCommand
import org.rivelles.guesswho.domain.repositories.QuestionRepository
import org.rivelles.guesswho.domain.repositories.SessionRepository
import java.lang.IllegalStateException
import java.time.LocalDate

class CreateSessionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val questionRepository: QuestionRepository
) {
    fun handle(createSessionCommand: CreateSessionCommand) {
        checkIfThereIsASessionTodayFor(createSessionCommand.userIdentifier)

        val questionOfTheDay = questionRepository.getQuestionOfTheDay()
            ?: throw IllegalStateException("Question of the day not found")

        val session = Session(
            userIdentifier = createSessionCommand.userIdentifier,
            question = questionOfTheDay
        )

        sessionRepository.save(session)
    }

    private fun checkIfThereIsASessionTodayFor(userIdentifier: UserIdentifier) {
        sessionRepository.findTodaySessionForUser(userIdentifier)?.let {
            throw RuntimeException("User already has session for today")
        }
    }
}