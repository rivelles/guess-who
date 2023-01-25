package adapters.postgresql

import fixtures.aQuestionWithTips
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import org.rivelles.adapters.postgresql.QuestionR2DBCRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec
import org.springframework.r2dbc.core.FetchSpec
import reactor.core.publisher.Flux

// TODO: Make it work
class QuestionR2DBCRepositoryTest :
    BehaviorSpec({
        val databaseClient = mockk<DatabaseClient>(relaxed = true)
        val genericExecuteSpec = mockk<DatabaseClient.GenericExecuteSpec>(relaxed = true)
        val fetchSpec = mockk<FetchSpec<MutableMap<String, Any>>>(relaxed = true)

        val questionR2dbcRepository = QuestionR2DBCRepository(databaseClient)

        given("A question of the day requested") {
            `when`("Query returns one value") {
                then("Should return question") {
                    val question = aQuestionWithTips(LocalDate.now(), listOf("Tip"))
                    val questionsFlux: Flux<MutableMap<String, Any>> =
                        Flux.just(
                            mutableMapOf(
                                Pair("question_id", question.questionId.id.toString()),
                                Pair("description", question.questionDescription.description),
                                Pair("answer", question.questionAnswer.answer),
                                Pair("image", question.questionImage.imageUrl),
                                Pair("date_appearance", LocalDate.now()),
                                Pair("tip", question.questionTips.tips[0])))
                    every { databaseClient.sql { any() } } returns genericExecuteSpec
                    every { genericExecuteSpec.bind(any<String>(), any<LocalDate>()) } returns
                        genericExecuteSpec
                    every { genericExecuteSpec.fetch() } returns fetchSpec
                    every { fetchSpec.all() } returns questionsFlux

                    val returnedQuestion = questionR2dbcRepository.getQuestionOfTheDay()

                    returnedQuestion shouldBe question
                }
            }
        }
    })
