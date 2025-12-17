package com.alperburaak.restapp.data.remote


import com.alperburaak.restapp.data.local.TokenDataStore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenStore.tokenFlow.first() }
        android.util.Log.d("AuthInterceptor", "token=${token?.take(10)}...")

        val newRequest = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .apply {
                if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token")
            }
            .build()

        return chain.proceed(newRequest)
    }
}
