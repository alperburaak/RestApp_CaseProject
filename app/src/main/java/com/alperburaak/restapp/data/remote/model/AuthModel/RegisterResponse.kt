package com.alperburaak.restapp.data.remote.model.AuthModel

data class RegisterResponse(
    val success: Boolean,
    val message: String?,
    val token: String,
    val user: User
)


