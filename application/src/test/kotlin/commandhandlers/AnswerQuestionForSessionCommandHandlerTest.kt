package commandhandlers

import QuestionAnswer
import Session
import commands.AnswerQuestionForSessionCommand
import fixtures.aQuestionWithoutTips
import fixtures.anUserIdentifier
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.lang.RuntimeException
import repositories.SessionRepository

class AnswerQuestionForSessionCommandHandlerTest :
    BehaviorSpec({
        val userIdentifier = anUserIdentifier()
        val question = aQuestionWithoutTips()
        val sessionRepository = mockk<SessionRepository>(relaxed = true)
        val commandHandler = AnswerQuestionForSessionCommandHandler(sessionRepository)

        given("An AnswerQuestionForSessionCommand being handled") {
            `when`("A correct answer is provided") {
                then("Should save session") {
                    val session = Session(userIdentifier, question)
                    every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns
                        session

                    val command =
                        AnswerQuestionForSessionCommand(userIdentifier, QuestionAnswer("Answer"))
                    commandHandler.handle(command)

                    verify { sessionRepository.save(session) }
                }
            }
            `when`("An incorrect answer is provided") {
                then("Should not save session") {
                    val session = Session(userIdentifier, question)
                    every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns
                        session

                    val command =
                        AnswerQuestionForSessionCommand(
                            userIdentifier, QuestionAnswer("Wrong answer"))
                    commandHandler.handle(command)

                    verify(exactly = 0) { sessionRepository.save(session) }
                }
            }
            `when`("A session is not found for the user") {
                then("Should throw RuntimeException") {
                    every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns null

                    val command =
                        AnswerQuestionForSessionCommand(userIdentifier, QuestionAnswer("Answer"))
                    val exception = shouldThrow<RuntimeException> { commandHandler.handle(command) }

                    exception.javaClass shouldBe RuntimeException::class.java
                    exception.message shouldBe "Session not found for user"
                }
            }
        }
    })
