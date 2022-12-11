package org.rivelles.guesswho.application.commandhandlers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.rivelles.guesswho.domain.QuestionAnswer
import org.rivelles.guesswho.domain.Session
import org.rivelles.guesswho.domain.commands.AnswerQuestionForSessionCommand
import org.rivelles.guesswho.domain.repositories.SessionRepository
import org.rivelles.guesswho.fixtures.aQuestion
import org.rivelles.guesswho.fixtures.anUserIdentifier
import java.lang.RuntimeException

class AnswerQuestionForSessionCommandHandlerTest : BehaviorSpec({

    val userIdentifier = anUserIdentifier()
    val question = aQuestion()
    val sessionRepository = mockk<SessionRepository>(relaxed = true)
    val commandHandler = AnswerQuestionForSessionCommandHandler(sessionRepository)

    given("An AnswerQuestionForSessionCommand being handled") {
        `when`("A correct answer is provided") {
            then("Should save session and return true") {
                val session = Session(userIdentifier, question)
                every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns session

                val command = AnswerQuestionForSessionCommand(userIdentifier, QuestionAnswer("Answer"))
                val returnValue = commandHandler.handle(command)

                returnValue shouldBe true
                verify { sessionRepository.save(session) }
            }
        }
        `when`("An incorrect answer is provided") {
            then("Should not save session and return false") {
                val session = Session(userIdentifier, question)
                every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns session

                val command = AnswerQuestionForSessionCommand(userIdentifier, QuestionAnswer("Wrong answer"))
                val returnValue = commandHandler.handle(command)

                returnValue shouldBe false
                verify(exactly = 0) { sessionRepository.save(session) }
            }
        }
        `when`("A session is not found for the user") {
            then("Should throw RuntimeException") {
                every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns null

                val command = AnswerQuestionForSessionCommand(userIdentifier, QuestionAnswer("Answer"))
                val exception = shouldThrow<RuntimeException> { commandHandler.handle(command) }

                exception.javaClass shouldBe RuntimeException::class.java
                exception.message shouldBe "Session not found for user"
            }
        }
    }
})
