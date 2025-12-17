package com.alperburaak.restapp.data.remote.model.authModel



data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val user: User,
    val token: String
)


