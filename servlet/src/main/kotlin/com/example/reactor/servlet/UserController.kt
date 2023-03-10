package com.example.reactor.servlet

import com.example.reactor.model.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Paths
import javax.servlet.http.HttpServletRequest
import kotlin.random.Random

@RestController
class UserController(val userRepository: UserRepository) {
    @GetMapping("/svc")
    fun svc(): User {
        val user = User(name = Random.nextLong().toString())
        val u1 = userRepository.save(user)
        userRepository.delete(u1)
        return u1
    }

    @GetMapping("/sync/{delay}")
    fun sync(@PathVariable("delay") delay: Long) {
        Thread.sleep(delay)
    }

    @PostMapping("/upload")
    fun uploadFile(request: HttpServletRequest): Long {
        val tmpDir = System.getProperty("java.io.tmpdir")
        val filePath = Paths.get(tmpDir, Random.nextLong().toString())
        val fileOutputStream = Files.newOutputStream(filePath)
        request.inputStream.copyTo(fileOutputStream)
        return filePath.toFile().length()
    }
}
