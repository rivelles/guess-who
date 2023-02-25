package org.rivelles.http

import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.router

@Component
class SessionsRouter {

    @Bean
    fun route(sessionsHandler: SessionsHandler) = router {
        (accept(APPLICATION_JSON) and "/sessions").nest {
            POST("").invoke(sessionsHandler::save)

            POST("/{userIdentifier}:answer").invoke(sessionsHandler::answer)
        }
    }
}
