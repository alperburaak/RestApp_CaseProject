package com.alperburaak.restapp.data.remote.model.AuthModel



data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val user: User,
    val token: String
)


