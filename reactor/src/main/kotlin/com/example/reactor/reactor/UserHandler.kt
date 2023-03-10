package com.example.reactor.reactor

import com.example.reactor.SERVICE_URL
import com.example.reactor.model.User
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchangeOrNull
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToFlow
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.random.Random

class UserHandler(val userReactiveRepository: UserReactiveRepository) {
    val webClient = WebClient.create(SERVICE_URL)
    val tmpDir = System.getProperty("java.io.tmpdir")
    // NOCC:UnusedPrivateMember(设计如此:)
    suspend fun svc(serverRequest: ServerRequest): ServerResponse {
        val user = User(name = Random.nextLong().toString())
        val u1 = userReactiveRepository.save(user).awaitSingle()
        userReactiveRepository.delete(u1).awaitSingleOrNull()
        return ok().bodyValueAndAwait(u1)
    }

    suspend fun syncByRequest(serverRequest: ServerRequest): ServerResponse {
        val delay = serverRequest.pathVariable("delay").toLong()
        webClient.get().uri("/sync/$delay").awaitExchangeOrNull {
            check(it.statusCode().is2xxSuccessful)
        }
        return ok().buildAndAwait()
    }

    suspend fun sync(serverRequest: ServerRequest): ServerResponse {
        val delay = serverRequest.pathVariable("delay").toLong()
        Mono.fromRunnable<Void> { Thread.sleep(delay) }
            .subscribeOn(Schedulers.boundedElastic())
            .awaitSingleOrNull()
        return ok().buildAndAwait()
    }

    suspend fun uploadFile(serverRequest: ServerRequest): ServerResponse {
        val filePath = Paths.get(tmpDir, Random.nextLong().toString())
        val ch = AsynchronousFileChannel.open(filePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)
        val fileLength: Long
        try {
            var pos = 0L
            serverRequest.bodyToFlow<DataBuffer>().collect {
                try {
                    val readableByteCount = it.readableByteCount()
                    DataBufferUtils.write(Mono.just(it), ch, pos).awaitSingle()
                    pos += readableByteCount
                } finally {
                    DataBufferUtils.release(it)
                }
            }
        } finally {
            fileLength = filePath.toFile().length()
            ch.close()
            Files.deleteIfExists(filePath)
        }
        return ok().bodyValueAndAwait(fileLength)
    }
}
