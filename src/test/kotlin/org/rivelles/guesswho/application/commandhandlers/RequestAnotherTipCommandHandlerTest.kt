package org.rivelles.guesswho.application.commandhandlers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.lang.RuntimeException
import java.time.LocalDate
import org.rivelles.guesswho.domain.Session
import org.rivelles.guesswho.domain.commands.RequestAnotherTipCommand
import org.rivelles.guesswho.domain.repositories.SessionRepository
import org.rivelles.guesswho.fixtures.aQuestionWithOneTip
import org.rivelles.guesswho.fixtures.anUserIdentifier

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
                    val returnedSession = commandHandler.handle(command)

                    returnedSession.showedTips shouldBe "Test tip"
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
