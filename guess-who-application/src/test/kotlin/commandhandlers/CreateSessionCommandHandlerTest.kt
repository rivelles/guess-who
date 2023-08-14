package commandhandlers

import Session
import commands.CreateSessionCommand
import fixtures.aQuestionWithoutTips
import fixtures.anUserIdentifier
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import java.time.Duration
import java.time.LocalDate
import kotlin.RuntimeException
import org.rivelles.adapters.persistence.QuestionRepository
import org.rivelles.adapters.persistence.SessionRepository
import org.rivelles.commandhandlers.CreateSessionCommandHandler
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

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
                    every { sessionRepository.findTodaySessionForUser(any()) } returns Mono.empty()

                    val now: LocalDate = mockk()
                    every { LocalDate.now() } returns now

                    every { questionRepository.getQuestionOfTheDay() } returns Mono.just(question)

                    every { sessionRepository.save(any()) } returns Mono.just(1)

                    val returnedValue = commandHandler.handle(CreateSessionCommand(userIdentifier))

                    StepVerifier.create(returnedValue)
                        .expectNext(1)
                        .expectComplete()
                        .verify(Duration.ofSeconds(2L))
                }
            }
            `when`("There is already a session for the user in the same day") {
                then("Should throw Exception") {
                    val existingSession = Session(userIdentifier, question)

                    every { sessionRepository.findTodaySessionForUser(any()) } returns
                        Mono.just(existingSession)

                    val returnedValue = commandHandler.handle(CreateSessionCommand(userIdentifier))

                    StepVerifier.create(returnedValue)
                        .expectError(RuntimeException::class.java)
                        .verify()
                }
            }
            `when`("There is no question of the day") {
                then("Should throw Exception") {
                    every { sessionRepository.findTodaySessionForUser(any()) } returns Mono.empty()

                    every { questionRepository.getQuestionOfTheDay() } returns Mono.empty()

                    val returnedValue = commandHandler.handle(CreateSessionCommand(userIdentifier))

                    StepVerifier.create(returnedValue)
                        .expectError(IllegalStateException::class.java)
                        .verify()
                }
            }
        }
    })
