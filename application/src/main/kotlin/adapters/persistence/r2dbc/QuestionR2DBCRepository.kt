package org.rivelles.adapters.persistence.r2dbc

import Question
import QuestionAnswer
import QuestionDateOfAppearance
import QuestionDescription
import QuestionId
import QuestionImage
import QuestionTips
import java.time.LocalDate
import java.util.*
import org.rivelles.adapters.persistence.QuestionRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono

@Repository
class QuestionR2DBCRepository(
    val databaseClient: DatabaseClient,
    val tm: ReactiveTransactionManager
) : QuestionRepository {
    override fun getQuestionOfTheDay(): Mono<Question?> =
        databaseClient
            .sql {
                """
                        SELECT * FROM questions q
                        INNER JOIN question_tips qt ON qt.question_id = q.id
                        WHERE date_appearance = :date_appearance
                    """.trimIndent()
            }
            .bind("date_appearance", LocalDate.now())
            .fetch()
            .all()
            .mapNotNull { row -> row.toQuestion() }
            .reduce { question1, question2 -> question1?.mergeQuestionTips(question2) }

    override fun save(question: Question): Mono<Int> {
        val questionMono =
            databaseClient
                .sql {
                    """
                        INSERT INTO questions
                        (id, description, answer, image, date_appearance)
                        VALUES
                        (:id, :description, :answer, :image, :date_appearance)
                    """.trimIndent()
                }
                .bind("id", question.questionId.id)
                .bind("description", question.questionDescription.description)
                .bind("answer", question.questionAnswer.answer)
                .bind("image", question.questionImage.imageUrl)
                .bind("date_appearance", question.questionDateOfAppearance.dateOfAppearance)
                .fetch()
                .rowsUpdated()

        if (question.questionTips.tips.isNotEmpty()) {
            val rxtx = TransactionalOperator.create(tm)

            val tipsMono =
                databaseClient
                    .sql {
                        """
                    INSERT INTO question_tips
                    (id, question_id, tip)
                    VALUES
                    ${question.insertTipsStatement()}
                """.trimIndent()
                    }
                    .fetch()
                    .rowsUpdated()

            return questionMono.then(tipsMono).`as`(rxtx::transactional)
        }

        return questionMono
    }

    private fun Question.insertTipsStatement() =
        this.questionTips.tips
            .map { "('${UUID.randomUUID()}', '${this.questionId.id}', '$it')" }
            .reduce { acc, s -> "$acc,$s" }

    private fun MutableMap<String, Any>.toQuestion(): Question? {
        if (this.isEmpty()) return null

        val questionId = UUID.fromString(this["question_id"].toString())
        val description = this["description"].toString()
        val answer = this["answer"].toString()
        val image = this["image"].toString()
        val dateOfAppearance = LocalDate.parse(this["date_appearance"].toString().split("T")[0])
        val tip = this["tip"]?.toString()

        return Question(
            QuestionId(questionId),
            QuestionDescription(description),
            QuestionAnswer(answer),
            QuestionTips(tip?.let { listOf(it) } ?: emptyList()),
            QuestionImage(image),
            QuestionDateOfAppearance(dateOfAppearance))
    }

    private fun Question?.mergeQuestionTips(question: Question?): Question? {
        if (this == null || question == null) return null

        return this.copy(
            questionTips = QuestionTips(this.questionTips.tips + question.questionTips.tips))
    }
}
