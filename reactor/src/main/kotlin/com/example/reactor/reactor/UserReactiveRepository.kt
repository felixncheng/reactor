package com.example.reactor.reactor

import com.example.reactor.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface UserReactiveRepository : ReactiveCrudRepository<User, String>
