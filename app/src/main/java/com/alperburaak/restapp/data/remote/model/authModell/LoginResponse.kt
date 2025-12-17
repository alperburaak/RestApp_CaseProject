package com.alperburaak.restapp.data.remote.model.authModell



data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val user: User,
    val token: String
)


