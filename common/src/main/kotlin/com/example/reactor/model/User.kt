package com.example.reactor.model

import org.springframework.data.mongodb.core.mapping.Document

@Document("t_user")
data class User(
    val id: String? = null,
    val name: String,
)
