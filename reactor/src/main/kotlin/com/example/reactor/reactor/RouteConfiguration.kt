package com.example.reactor.reactor

import org.springframework.web.reactive.function.server.coRouter

class RouteConfiguration(val userHandler: UserHandler) {
    fun router() = coRouter {
        GET("/svc", userHandler::svc)
        GET("/sync-by-request/{delay}", userHandler::syncByRequest)
        GET("/sync/{delay}", userHandler::sync)
        POST("/upload", userHandler::uploadFile)
    }
}
