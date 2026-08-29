package com.focusflow.app.data.remote.api

import com.focusflow.app.data.remote.dto.groq.GroqChatRequest
import com.focusflow.app.data.remote.dto.groq.GroqChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApiService {

    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: GroqChatRequest
    ): Response<GroqChatResponse>
}
