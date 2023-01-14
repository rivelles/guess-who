package adapters.http

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.spring.SpringListener
import org.rivelles.GuessWhoApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@ContextConfiguration(classes = [(GuessWhoApplication::class)])
@TestPropertySource("classpath:application.properties")
@WebFluxTest
class SessionsRouterIT : FunSpec() {
    override fun listeners() = listOf(SpringListener)

    @Autowired lateinit var applicationContext: ApplicationContext

    lateinit var client: WebTestClient

    init {
        beforeAny { client = WebTestClient.bindToApplicationContext(applicationContext).build() }
        test("Should run") { true shouldBe true }
        test("Should have webclient") {
            client.post().uri("/sessions").exchange().expectStatus().is4xxClientError
        }
    }
}
