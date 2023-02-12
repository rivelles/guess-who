package org.rivelles.adapters.http

import Session
import UserIdentifier
import fixtures.aQuestionWithTips
import fixtures.aQuestionWithoutTips
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import java.time.LocalDate
import org.rivelles.adapters.http.requests.AnswerQuestionForSessionRequest
import org.rivelles.adapters.http.requests.CreateSessionForUserRequest
import org.rivelles.adapters.persistence.QuestionRepository
import org.rivelles.adapters.persistence.SessionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(locations = ["classpath:application.yaml"])
class SessionsRouterIT : StringSpec() {

    override fun extensions() = listOf(SpringExtension)

    @Autowired lateinit var webTestClient: WebTestClient
    @Autowired lateinit var questionRepository: QuestionRepository
    @Autowired lateinit var sessionRepository: SessionRepository

    private val postgreSQLContainer: PostgreSQLContainer<*> =
        PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("guess_who")
            .withUsername("user")
            .withPassword("password")

    init {
        postgreSQLContainer.start()
        System.setProperty("spring.r2dbc.url", postgreSQLContainer.jdbcUrl.replace("jdbc", "r2dbc"))
        System.setProperty("spring.flyway.url", postgreSQLContainer.jdbcUrl)

        "When creating session with empty body, should receive client error" {
            webTestClient.post().uri("/sessions").exchange().expectStatus().is4xxClientError
        }
        "Should create session" {
            val requestBody = CreateSessionForUserRequest("127.0.0.1")
            val question =
                aQuestionWithTips(
                    LocalDate.now(), listOf("Tip 1", "Tip 2", "Tip 3", "Tip 4", "Tip 5"))
            questionRepository.save(question).toFuture().get()

            webTestClient
                .post()
                .uri("/sessions")
                .contentType(APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus()
                .is2xxSuccessful
        }
        "Should answer to question in session" {
            val requestBody = AnswerQuestionForSessionRequest("Answer")
            val question = aQuestionWithoutTips(LocalDate.now())

            val session = Session(UserIdentifier("127.0.0.2"), question)
            questionRepository.save(question).then(sessionRepository.save(session)).toFuture().get()

            webTestClient
                .post()
                .uri("/sessions/127.0.0.2:answer")
                .contentType(APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus()
                .is2xxSuccessful
        }
    }
}
