package com.alperburaak.restapp.data.remote.ws



import com.alperburaak.restapp.data.local.TokenDataStore
import com.pusher.client.AuthorizationFailureException
import com.pusher.client.Authorizer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class PusherAuthorizer(
    private val tokenStore: TokenDataStore
) : Authorizer {

    private val client = OkHttpClient()
    private val authUrl = "http://188.34.155.223/new-qr-menu/api/broadcasting/auth"
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()

    override fun authorize(channelName: String, socketId: String): String {
        val token = runBlocking { tokenStore.tokenFlow.first() }.orEmpty()

        val bodyJson = JSONObject()
            .put("socket_id", socketId)
            .put("channel_name", channelName)
            .toString()

        val request = Request.Builder()
            .url(authUrl)
            .post(bodyJson.toRequestBody(jsonMedia))
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .addHeader("Origin", "http://188.34.155.223")
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw AuthorizationFailureException("Auth failed HTTP ${response.code}")
        }

        return response.body?.string() ?: throw AuthorizationFailureException("Empty auth body")
    }
}
