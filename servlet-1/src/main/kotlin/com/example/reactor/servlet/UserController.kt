package com.example.reactor.servlet

import com.example.reactor.SERVICE_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {
    private val client = OkHttpClient().newBuilder()
        .build()

    @GetMapping("/sync-by-request/{delay}")
    fun syncByRequest(@PathVariable("delay") delay: Long) {
        val request = Request.Builder()
            .url("$SERVICE_URL/sync/$delay")
            .build()
        val res = client.newCall(request).execute()
        check(res.isSuccessful)
    }
}
