package commandhandlers

import Question
import QuestionAnswer
import QuestionDateOfAppearance
import QuestionDescription
import QuestionImage
import QuestionTips
import commands.CreateQuestionCommand
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.Duration
import java.time.LocalDate
import org.rivelles.adapters.persistence.QuestionRepository
import org.rivelles.commandhandlers.CreateQuestionCommandHandler
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class CreateQuestionCommandHandlerTest :
    BehaviorSpec({
        val questionRepository = mockk<QuestionRepository>(relaxed = true)

        val commandHandler = CreateQuestionCommandHandler(questionRepository)

        given("A CreateQuestionCommand being handled") {
            `when`("Correct data are provided") {
                then("Should create a Question") {
                    val command =
                        CreateQuestionCommand(
                            QuestionDescription("Description"),
                            QuestionAnswer("Answer"),
                            QuestionTips(listOf("Tip 1")),
                            QuestionImage("http://test-image.com"),
                            QuestionDateOfAppearance(LocalDate.MAX))

                    val savedQuestion = slot<Question>()

                    every { questionRepository.save(capture(savedQuestion)) } returns Mono.just(1)

                    val returnedValue = commandHandler.handle(command)

                    StepVerifier.create(returnedValue)
                        .expectNext(1)
                        .expectComplete()
                        .verify(Duration.ofSeconds(2L))

                    savedQuestion.captured.questionDescription shouldBe command.questionDescription
                    savedQuestion.captured.questionAnswer shouldBe command.questionAnswer
                    savedQuestion.captured.questionImage shouldBe command.questionImage
                    savedQuestion.captured.questionTips shouldBe command.questionTips
                    savedQuestion.captured.questionDateOfAppearance shouldBe
                        QuestionDateOfAppearance(LocalDate.MAX)
                }
            }
        }
    })
