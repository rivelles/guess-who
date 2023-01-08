package org.rivelles

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class GuessWhoApplication

fun main(args: Array<String>) {
    runApplication<GuessWhoApplication>(*args)
}
