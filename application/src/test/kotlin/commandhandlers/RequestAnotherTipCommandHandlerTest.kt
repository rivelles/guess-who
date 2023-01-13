package commandhandlers

import Session
import commands.RequestAnotherTipCommand
import fixtures.aQuestionWithOneTip
import fixtures.anUserIdentifier
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import java.lang.RuntimeException
import java.time.LocalDate
import org.rivelles.commandhandlers.RequestAnotherTipCommandHandler
import repositories.SessionRepository

class RequestAnotherTipCommandHandlerTest :
    BehaviorSpec({
        val sessionRepository = mockk<SessionRepository>(relaxed = true)

        val question = aQuestionWithOneTip("Test tip")
        val userIdentifier = anUserIdentifier()

        val commandHandler = RequestAnotherTipCommandHandler(sessionRepository)

        mockkStatic(LocalDate::class)

        given("A RequestAnotherTipCommand being handled") {
            `when`("There is a session for today and not all tips are shown") {
                then("Should execute successfully") {
                    val session = Session(userIdentifier, question)
                    every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns
                        session

                    val command = RequestAnotherTipCommand(userIdentifier)
                    commandHandler.handle(command)

                    verify { sessionRepository.save(session) }
                }
            }
            `when`("There is no session for user today") {
                then("Should throw exception") {
                    every { sessionRepository.findTodaySessionForUser(userIdentifier) } returns null

                    val exception =
                        shouldThrow<RuntimeException> {
                            commandHandler.handle(RequestAnotherTipCommand(userIdentifier))
                        }

                    exception.javaClass shouldBe RuntimeException::class.java
                    exception.message shouldBe "Session not found for user"
                }
            }
        }
    })
