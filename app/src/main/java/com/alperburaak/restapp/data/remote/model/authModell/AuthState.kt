package com.alperburaak.restapp.data.remote.model.authModell

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val token: String) : AuthState()
    data class Loading(val error: String? = null) : AuthState()
}


