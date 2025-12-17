package com.alperburaak.restapp.data.repository

import com.alperburaak.restapp.data.remote.api.AuthApi
import com.alperburaak.restapp.data.remote.model.authModell.LoginRequest
import com.alperburaak.restapp.data.remote.model.authModell.LoginResponse
import com.alperburaak.restapp.data.remote.model.authModell.RegisterRequest
import com.alperburaak.restapp.data.remote.model.authModell.RegisterResponse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.alperburaak.restapp.data.local.TokenDataStore

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<LoginResponse> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.login(request)
                if (response.isSuccessful) {
                    response.body() ?: throw IllegalStateException("Empty response body")
                } else {
                    throw IllegalStateException("HTTP ${response.code()} - ${response.message()}")
                }
            }
        }

    override suspend fun register(request: RegisterRequest): Result<RegisterResponse> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.register(request)
                if (response.isSuccessful) {
                    response.body() ?: throw IllegalStateException("Empty response body")
                } else {
                    throw IllegalStateException("HTTP ${response.code()} - ${response.message()}")
                }
            }
        }
}

