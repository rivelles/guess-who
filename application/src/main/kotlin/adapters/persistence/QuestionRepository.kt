package org.rivelles.adapters.persistence

import Question
import reactor.core.publisher.Mono

interface QuestionRepository {
    fun getQuestionOfTheDay(): Mono<Question?>
    fun save(question: Question): Mono<Int>
}
