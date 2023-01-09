package org.rivelles.adapters.http

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.coRouter

@Component
class SessionsRouter {

    @Bean
    fun coRoute(sessionsHandler: SessionsHandler) = coRouter {
        (accept(APPLICATION_JSON) and "/sessions").nest { POST("").invoke(sessionsHandler::save) }
    }
}
