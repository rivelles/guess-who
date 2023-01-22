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
                .map { rows ->
                    val tips = rows["tip"].toString()
                    val questionId = UUID.fromString(rows["question_id"].toString())
                    val description = rows["description"].toString()
                    val answer = rows["answer"].toString()
                    val image = rows["image"].toString()
                    val dateOfAppearance =
                        LocalDate.parse(rows["date_appearance"].toString().split("T")[0])

                    Question(
                        QuestionId(questionId),
                        QuestionDescription(description),
                        QuestionAnswer(answer),
                        QuestionTips(listOf(tips)),
                        QuestionImage(image),
                        QuestionDateOfAppearance(dateOfAppearance))
                }
                .reduce { q1, q2 ->
                    val questionTips =
                        QuestionTips(listOf(q1.questionTips.tips[0], q2.questionTips.tips[0]))
                    q1.copy(questionTips = questionTips)
                }
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
}
