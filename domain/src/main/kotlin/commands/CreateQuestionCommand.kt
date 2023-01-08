package commands

import QuestionAnswer
import QuestionDateOfAppearance
import QuestionDescription
import QuestionImage
import QuestionTips

data class CreateQuestionCommand(
    val questionDescription: QuestionDescription,
    val questionAnswer: QuestionAnswer,
    val questionTips: QuestionTips,
    val questionImage: QuestionImage,
    val dateOfAppearance: QuestionDateOfAppearance
) : Command
