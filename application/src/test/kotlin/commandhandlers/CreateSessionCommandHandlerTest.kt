package commandhandlers

import Session
import commands.CreateSessionCommand
import fixtures.aQuestionWithoutTips
import fixtures.anUserIdentifier
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.lang.RuntimeException
import java.time.LocalDate
import org.rivelles.adapters.persistence.QuestionRepository
import org.rivelles.adapters.persistence.SessionRepository
import org.rivelles.commandhandlers.CreateSessionCommandHandler
import reactor.core.publisher.Mono

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

                    val expectedSession = Session(userIdentifier, question)

                    commandHandler.handle(CreateSessionCommand(userIdentifier)).subscribe {
                        it shouldBe 1
                        verify { sessionRepository.save(expectedSession) }
                    }
                }
            }
            `when`("There is already a session for the user in the same day") {
                then("Should throw Exception") {
                    val existingSession = Session(userIdentifier, question)

                    every { sessionRepository.findTodaySessionForUser(any()) } returns
                        Mono.just(existingSession)

                    commandHandler
                        .handle(CreateSessionCommand(userIdentifier))
                        .doOnError { it.javaClass shouldBe RuntimeException::class.java }
                        .subscribe { verify { sessionRepository.save(any()) wasNot called } }
                }
            }
            `when`("There is no question of the day") {
                then("Should throw Exception") {
                    every { sessionRepository.findTodaySessionForUser(any()) } returns Mono.empty()

                    every { questionRepository.getQuestionOfTheDay() } returns Mono.empty()

                    commandHandler
                        .handle(CreateSessionCommand(userIdentifier))
                        .doOnError { it.javaClass shouldBe IllegalStateException::class.java }
                        .subscribe { verify { sessionRepository.save(any()) wasNot called } }
                }
            }
        }
    })
