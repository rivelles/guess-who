package commandhandlers

import Session
import commands.RequestAnotherTipCommand
import fixtures.aQuestionWithTips
import fixtures.anUserIdentifier
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import java.lang.RuntimeException
import java.time.Duration
import java.time.LocalDate
import org.rivelles.adapters.persistence.SessionRepository
import org.rivelles.commandhandlers.RequestAnotherTipCommandHandler
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class RequestAnotherTipCommandHandlerTest :
    BehaviorSpec({
        val sessionRepository = mockk<SessionRepository>(relaxed = true)

        val question = aQuestionWithTips("Test tip")
        val userIdentifier = anUserIdentifier()

        val commandHandler = RequestAnotherTipCommandHandler(sessionRepository)

        mockkStatic(LocalDate::class)

        given("A RequestAnotherTipCommand being handled") {
            `when`("There is a session for today and not all tips are shown") {
                then("Should execute successfully") {
                    val session = Session(userIdentifier, question)
                    every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns
                        Mono.just(session)

                    every { sessionRepository.save(session) } returns Mono.just(1)

                    val returnedValue =
                        commandHandler.handle(RequestAnotherTipCommand(userIdentifier))

                    StepVerifier.create(returnedValue)
                        .expectNext(1)
                        .expectComplete()
                        .verify(Duration.ofSeconds(2L))
                }
            }
            `when`("There is no session for user today") {
                then("Should throw exception") {
                    every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns
                        Mono.empty()

                    val returnedValue =
                        commandHandler.handle(RequestAnotherTipCommand(userIdentifier))

                    StepVerifier.create(returnedValue)
                        .expectError(RuntimeException::class.java)
                        .verify(Duration.ofSeconds(2L))
                }
            }
        }
    })
