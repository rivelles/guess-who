package org.rivelles.adapters.http

import fixtures.aQuestionWithTips
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import java.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import repositories.QuestionRepository
import repositories.SessionRepository

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

        "Should create session" {
            val requestBody = CreateSessionForUserRequest("127.0.0.1")
            val question = aQuestionWithTips(LocalDate.now(), listOf("Tip 1", "Tip 2"))
            questionRepository.save(question)

            webTestClient
                .post()
                .uri("/sessions")
                .contentType(APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus()
                .is2xxSuccessful
        }
        "When creating session with empty body, should receive client error" {
            webTestClient.post().uri("/sessions").exchange().expectStatus().is4xxClientError
        }
    }
}
