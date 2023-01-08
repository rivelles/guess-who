import fixtures.aQuestionWithOneTip
import fixtures.aQuestionWithoutTips
import fixtures.anUserIdentifier
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class SessionTest :
    BehaviorSpec({
        val userIdentifier = anUserIdentifier()
        val question = aQuestionWithoutTips()

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

        given("A tip is requested") {
            `when`("The number of showed tips is lower than the total number of tips") {
                then("Should show another tip") {
                    val tip = "Test tip"
                    val questionWithOneTip = aQuestionWithOneTip(tip)

                    val session = Session(userIdentifier, questionWithOneTip)
                    session.requestOneMoreTip()

                    session.showedTips shouldBe QuestionTips(listOf(tip))
                }
            }
            `when`("The number of showed tips is equal than the total number of tips") {
                then("Should throw exception") {
                    val tip = "Test tip"
                    val questionWithOneTip = aQuestionWithOneTip(tip)

                    val session = Session(userIdentifier, questionWithOneTip)
                    session.requestOneMoreTip()

                    val exception =
                        shouldThrow<IllegalStateException> { session.requestOneMoreTip() }

                    exception.message shouldBe "All tips are already shown."
                }
            }
        }
    })
