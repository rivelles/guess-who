package org.rivelles.guesswho.domain.commands

import org.rivelles.guesswho.domain.*

data class CreateQuestionCommand(
    val questionDescription: QuestionDescription,
    val questionAnswer: QuestionAnswer,
    val questionTips: QuestionTips,
    val questionImage: QuestionImage,
    val dateOfAppearance: QuestionDateOfAppearance
) : Command
