package com.alperburaak.restapp.data.repository

import com.alperburaak.restapp.data.remote.model.authModell.LoginRequest
import com.alperburaak.restapp.data.remote.model.authModell.LoginResponse
import com.alperburaak.restapp.data.remote.model.authModell.RegisterRequest
import com.alperburaak.restapp.data.remote.model.authModell.RegisterResponse

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
    suspend fun register(request: RegisterRequest): Result<RegisterResponse>
}
