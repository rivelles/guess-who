package adapters.http

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.spring.SpringListener

class SessionsRouterIT : FunSpec() {
    override fun listeners() = listOf(SpringListener)

    init {
        test("Should run") { true shouldBe true }
    }
}
