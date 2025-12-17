package com.alperburaak.restapp.ui.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val token: String? = null
)
