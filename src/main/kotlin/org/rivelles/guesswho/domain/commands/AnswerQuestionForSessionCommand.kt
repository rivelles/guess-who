package org.rivelles.guesswho.domain.commands

import org.rivelles.guesswho.domain.QuestionAnswer
import org.rivelles.guesswho.domain.UserIdentifier

data class AnswerQuestionForSessionCommand(
    val userIdentifier: UserIdentifier,
    val providedAnswer: QuestionAnswer)