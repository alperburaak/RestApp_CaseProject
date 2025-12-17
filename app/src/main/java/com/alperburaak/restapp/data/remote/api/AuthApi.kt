package com.alperburaak.restapp.data.remote.api



import com.alperburaak.restapp.data.remote.model.AuthModel.LoginRequest
import com.alperburaak.restapp.data.remote.model.AuthModel.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/customer/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}
