package org.rivelles.guesswho.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.rivelles.guesswho.fixtures.aQuestion
import org.rivelles.guesswho.fixtures.anUserIdentifier

class SessionTest :
    BehaviorSpec({
        val userIdentifier = anUserIdentifier()
        val question = aQuestion()

        given("An answer is provided") {
            `when`("Answer is correct") {
                then("Session should be finished") {
                    val providedAnswer = QuestionAnswer("Answer")

                    val session = Session(userIdentifier, question)
                    session.answerQuestion(providedAnswer)

                    session.isFinished() shouldBe true
                }
            }
            `when`("Answer is not correct") {
                then("Session should not be finished") {
                    val providedAnswer = QuestionAnswer("Wrong answer")

                    val session = Session(userIdentifier, question)
                    session.answerQuestion(providedAnswer)

                    session.isFinished() shouldBe false
                }
            }
        }
    })
