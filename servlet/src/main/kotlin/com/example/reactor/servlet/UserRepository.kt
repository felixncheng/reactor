package com.example.reactor.servlet

import com.example.reactor.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, String>
