package commands

import QuestionAnswer
import UserIdentifier

data class AnswerQuestionForSessionCommand(
    val userIdentifier: UserIdentifier,
    val providedAnswer: QuestionAnswer
) : Command
