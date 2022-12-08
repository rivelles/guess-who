package org.rivelles.guesswho.application.commandhandlers

import org.rivelles.guesswho.domain.Session
import org.rivelles.guesswho.domain.SessionDate
import org.rivelles.guesswho.domain.commands.CreateSessionCommand
import org.rivelles.guesswho.domain.repositories.QuestionRepository
import org.rivelles.guesswho.domain.repositories.SessionRepository
import java.lang.IllegalStateException
import java.rmi.UnexpectedException
import java.time.LocalDate
import java.util.Objects.nonNull

class CreateSessionCommandHandler(
    private val sessionRepository: SessionRepository,
    private val questionRepository: QuestionRepository
) {
    fun handle(createSessionCommand: CreateSessionCommand) {
        sessionRepository.findTodaySessionForUser(createSessionCommand.userIdentifier)?.let {
            throw RuntimeException("User already has session for today")
        }

        val sessionDate = SessionDate(LocalDate.now())
        val questionOfTheDay = questionRepository.getQuestionOfTheDay() ?: throw IllegalStateException("Question of the day not found")

        val session = Session(createSessionCommand.userIdentifier, sessionDate, questionOfTheDay)

        sessionRepository.save(session)
    }
}