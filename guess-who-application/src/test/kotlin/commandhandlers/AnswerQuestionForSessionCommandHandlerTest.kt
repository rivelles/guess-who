package commandhandlers

import QuestionAnswer
import Session
import commands.AnswerQuestionForSessionCommand
import fixtures.aQuestionWithoutTips
import fixtures.anUserIdentifier
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.lang.RuntimeException
import java.time.Duration
import org.rivelles.adapters.persistence.SessionRepository
import org.rivelles.commandhandlers.AnswerQuestionForSessionCommandHandler
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

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
                        Mono.just(session)

                    every { sessionRepository.save(session) } returns Mono.just(1)

                    val returnedValue =
                        commandHandler.handle(
                            AnswerQuestionForSessionCommand(
                                userIdentifier, QuestionAnswer("Answer")))

                    StepVerifier.create(returnedValue)
                        .expectNext(1)
                        .expectComplete()
                        .verify(Duration.ofSeconds(2L))

                    verify { sessionRepository.save(session) }
                }
            }
            `when`("An incorrect answer is provided") {
                then("Should not save session") {
                    val session = Session(userIdentifier, question)
                    every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns
                        Mono.just(session)

                    val returnedValue =
                        commandHandler.handle(
                            AnswerQuestionForSessionCommand(
                                userIdentifier, QuestionAnswer("Wrong answer")))

                    StepVerifier.create(returnedValue)
                        .expectComplete()
                        .verify(Duration.ofSeconds(2L))

                    verify(exactly = 0) { sessionRepository.save(session) }
                }
            }
            `when`("A session is not found for the user") {
                then("Should throw RuntimeException") {
                    every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns
                        Mono.empty()

                    val returnedValue =
                        commandHandler.handle(
                            AnswerQuestionForSessionCommand(
                                userIdentifier, QuestionAnswer("Answer")))

                    StepVerifier.create(returnedValue)
                        .expectError(RuntimeException::class.java)
                        .verify(Duration.ofSeconds(2L))
                }
            }
        }
    })
