package org.rivelles.adapters.postgresql

import Question
import QuestionAnswer
import QuestionDateOfAppearance
import QuestionDescription
import QuestionId
import QuestionImage
import QuestionTips
import java.time.LocalDate
import java.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitRowsUpdated
import org.springframework.stereotype.Repository
import repositories.QuestionRepository

@Repository
class QuestionR2DBCRepository(val databaseClient: DatabaseClient) : QuestionRepository {
    override fun getQuestionOfTheDay(): Question? {
        val today = LocalDate.now()
        return runBlocking {
            databaseClient
                .sql {
                    """
                        SELECT * FROM questions q
                        INNER JOIN question_tips qt ON qt.question_id = q.id
                        WHERE date_appearance = :date_appearance
                    """.trimIndent()
                }
                .bind("date_appearance", today)
                .fetch()
                .all()
                .map { row -> row.toQuestion() }
                .reduce { question1, question2 -> question1.mergeQuestionTips(question2) }
                .awaitSingleOrNull()
        }
    }

    override fun save(question: Question) {
        runBlocking {
            val questionCreated =
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
                    .awaitRowsUpdated()

            if (questionCreated < 1) throw RuntimeException("Couldn't create question")
            question.questionTips.tips.forEach {
                launch {
                    databaseClient
                        .sql {
                            """
                                INSERT INTO question_tips
                                (id, question_id, tip)
                                VALUES
                                (:id, '${question.questionId.id}', :tip)
                            """.trimIndent()
                        }
                        .bind("id", UUID.randomUUID().toString())
                        .bind("tip", it)
                        .fetch()
                        .awaitRowsUpdated()
                }
            }
        }
    }

    private fun MutableMap<String, Any>.toQuestion(): Question {
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

    private fun Question.mergeQuestionTips(question: Question): Question =
        this.copy(questionTips = QuestionTips(this.questionTips.tips + question.questionTips.tips))
}
