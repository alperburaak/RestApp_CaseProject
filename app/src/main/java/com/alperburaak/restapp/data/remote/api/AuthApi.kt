package com.alperburaak.restapp.data.remote.api


import com.alperburaak.restapp.data.remote.model.authModell.LoginRequest
import com.alperburaak.restapp.data.remote.model.authModell.LoginResponse
import com.alperburaak.restapp.data.remote.model.authModell.RegisterRequest
import com.alperburaak.restapp.data.remote.model.authModell.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("v1/customer/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("v1/customer/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}
