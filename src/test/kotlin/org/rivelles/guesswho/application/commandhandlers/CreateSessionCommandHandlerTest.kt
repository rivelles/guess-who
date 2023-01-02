package org.rivelles.guesswho.application.commandhandlers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.lang.RuntimeException
import java.time.LocalDate
import org.rivelles.guesswho.domain.*
import org.rivelles.guesswho.domain.commands.CreateSessionCommand
import org.rivelles.guesswho.domain.repositories.QuestionRepository
import org.rivelles.guesswho.domain.repositories.SessionRepository
import org.rivelles.guesswho.fixtures.aQuestionWithoutTips
import org.rivelles.guesswho.fixtures.anUserIdentifier

internal class CreateSessionCommandHandlerTest :
    BehaviorSpec({
        val sessionRepository = mockk<SessionRepository>(relaxed = true)
        val questionRepository = mockk<QuestionRepository>()

        val question = aQuestionWithoutTips()
        val userIdentifier = anUserIdentifier()

        val commandHandler = CreateSessionCommandHandler(sessionRepository, questionRepository)

        mockkStatic(LocalDate::class)

        given("A CreateSessionCommand being handled") {
            `when`("Correct data are provided") {
                then("Should create a Session") {
                    every { sessionRepository.findTodaySessionForUser(any()) } returns null

                    val now: LocalDate = mockk()
                    every { LocalDate.now() } returns now

                    every { questionRepository.getQuestionOfTheDay() } returns question

                    val expectedSession = Session(userIdentifier, question)

                    commandHandler.handle(CreateSessionCommand(userIdentifier))

                    verify { sessionRepository.save(expectedSession) }
                }
            }
            `when`("There is already a session for the user in the same day") {
                then("Should throw Exception") {
                    val existingSession = Session(userIdentifier, question)

                    every { sessionRepository.findTodaySessionForUser(any()) } returns
                        existingSession

                    val exception =
                        shouldThrow<RuntimeException> {
                            commandHandler.handle(CreateSessionCommand(userIdentifier))
                        }

                    exception.javaClass shouldBe RuntimeException::class.java
                    exception.message shouldBe "User already has session for today"
                }
            }
            `when`("There is no question of the day") {
                then("Should throw Exception") {
                    every { sessionRepository.findTodaySessionForUser(any()) } returns null

                    every { questionRepository.getQuestionOfTheDay() } returns null

                    val exception =
                        shouldThrow<RuntimeException> {
                            commandHandler.handle(CreateSessionCommand(userIdentifier))
                        }

                    exception.javaClass shouldBe IllegalStateException::class.java
                    exception.message shouldBe "Question of the day not found"
                }
            }
        }
    })
