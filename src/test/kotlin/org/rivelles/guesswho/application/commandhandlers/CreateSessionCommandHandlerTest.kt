package org.rivelles.guesswho.application.commandhandlers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.rivelles.guesswho.domain.commands.CreateSessionCommand
import org.rivelles.guesswho.domain.*
import org.rivelles.guesswho.domain.repositories.QuestionRepository
import org.rivelles.guesswho.domain.repositories.SessionRepository
import java.lang.RuntimeException
import java.time.LocalDate
import java.util.UUID.randomUUID

internal class CreateSessionCommandHandlerTest : BehaviorSpec({
    val sessionRepository = mockk<SessionRepository>(relaxed = true)
    val questionRepository = mockk<QuestionRepository>()

    val question = Question(
        QuestionId(randomUUID()),
        QuestionDescription("Question"),
        QuestionAnswer("Answer"),
        QuestionTips(emptyList())
    )

    val userIdentifier = UserIdentifier("168.0.0.1")

    mockkStatic(LocalDate::class)

    given("A CreateSessionCommand being handled") {
        `when`("Correct data are provided") {
            then("Should create a Session") {

                every { sessionRepository.findTodaySessionForUser(any()) } returns null

                val now: LocalDate = mockk()
                every { LocalDate.now() } returns now

                every { questionRepository.getQuestionOfTheDay() } returns question

                val commandHandler = CreateSessionCommandHandler(sessionRepository, questionRepository)
                commandHandler.handle(CreateSessionCommand(userIdentifier))

                verify { sessionRepository.save(Session(userIdentifier, SessionDate(now), question)) }
            }
        }
        `when`("There is already a session for the user in the same day") {
            then("Should throw Exception") {
                val existingSession = Session(userIdentifier, SessionDate(LocalDate.now()), question)

                every { sessionRepository.findTodaySessionForUser(any()) } returns existingSession

                val commandHandler = CreateSessionCommandHandler(sessionRepository, questionRepository)
                val exception = shouldThrow<RuntimeException> { commandHandler.handle(CreateSessionCommand(userIdentifier)) }

                exception.javaClass shouldBe RuntimeException::class.java
                exception.message shouldBe "User already has session for today"
            }
        }
        `when`("There is no question of the day") {
            then("Should throw Exception") {
                every { sessionRepository.findTodaySessionForUser(any()) } returns null

                every { questionRepository.getQuestionOfTheDay() } returns null

                val commandHandler = CreateSessionCommandHandler(sessionRepository, questionRepository)
                val exception = shouldThrow<RuntimeException> { commandHandler.handle(CreateSessionCommand(userIdentifier)) }

                exception.javaClass shouldBe IllegalStateException::class.java
                exception.message shouldBe "Question of the day not found"
            }
        }
    }
})