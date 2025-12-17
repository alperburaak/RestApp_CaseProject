package com.alperburaak.restapp.data.remote.model.authModel



data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val phone: String,
    val bussiness_name: String,
    val bussiness_phone: String,
    val bussiness_email: String
)
