package fixtures

import Question
import QuestionAnswer
import QuestionDateOfAppearance
import QuestionDescription
import QuestionId
import QuestionImage
import QuestionTips
import UserIdentifier
import java.time.LocalDate
import java.util.Collections.emptyList
import java.util.UUID

fun aQuestionWithoutTips(): Question =
    Question(
        QuestionId(UUID.randomUUID()),
        QuestionDescription("Question"),
        QuestionAnswer("Answer"),
        QuestionTips(emptyList()),
        QuestionImage("http://fakeUrl.com/image"),
        QuestionDateOfAppearance(LocalDate.MAX))

fun aQuestionWithoutTips(dateOfAppearance: LocalDate): Question =
    Question(
        QuestionId(UUID.randomUUID()),
        QuestionDescription("Question"),
        QuestionAnswer("Answer"),
        QuestionTips(emptyList()),
        QuestionImage("http://fakeUrl.com/image"),
        QuestionDateOfAppearance(dateOfAppearance))

fun aQuestionWithTips(tip: String): Question =
    Question(
        QuestionId(UUID.randomUUID()),
        QuestionDescription("Question"),
        QuestionAnswer("Answer"),
        QuestionTips(listOf(tip)),
        QuestionImage("http://fakeUrl.com/image"),
        QuestionDateOfAppearance(LocalDate.MAX))

fun aQuestionWithTips(dateOfAppearance: LocalDate, tips: List<String>): Question =
    Question(
        QuestionId(UUID.randomUUID()),
        QuestionDescription("Question"),
        QuestionAnswer("Answer"),
        QuestionTips(tips),
        QuestionImage("http://fakeUrl.com/image"),
        QuestionDateOfAppearance(dateOfAppearance))

fun anUserIdentifier(): UserIdentifier = UserIdentifier("168.0.0.1")
