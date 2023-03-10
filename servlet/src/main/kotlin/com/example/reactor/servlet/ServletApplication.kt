package com.example.reactor.servlet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServletApplication

fun main(args: Array<String>) {
    runApplication<ServletApplication>(*args)
}
