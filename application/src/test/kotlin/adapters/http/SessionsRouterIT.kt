package adapters.http

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.spring.SpringListener
import org.rivelles.GuessWhoApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer

@ContextConfiguration(classes = [(GuessWhoApplication::class)])
@TestPropertySource("classpath:application.properties")
@WebFluxTest
class SessionsRouterIT : FunSpec() {
    override fun listeners() = listOf(SpringListener)

    @Autowired lateinit var applicationContext: ApplicationContext

    lateinit var client: WebTestClient

    private val postgreSQLContainer: PostgreSQLContainer<*> =
        PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("integration-tests-db")
            .withUsername("user")
            .withPassword("user")

    init {
        postgreSQLContainer.start()
        System.setProperty("spring.r2dbc.url", postgreSQLContainer.host)
        System.setProperty("spring.r2dbc.username", postgreSQLContainer.username)
        System.setProperty("spring.r2dbc.password", postgreSQLContainer.password)

        beforeAny { client = WebTestClient.bindToApplicationContext(applicationContext).build() }
        test("Should run") { applicationContext shouldNotBe null }
        test("When creating session with empty body, should receive client error") {
            client.post().uri("/sessions").exchange().expectStatus().is4xxClientError
        }
    }
}
