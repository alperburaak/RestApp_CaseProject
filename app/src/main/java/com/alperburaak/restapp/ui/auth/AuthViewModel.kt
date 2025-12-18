package com.alperburaak.restapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperburaak.restapp.data.local.TokenDataStore
import com.alperburaak.restapp.data.remote.model.authModell.AuthState
import com.alperburaak.restapp.data.remote.model.authModell.LoginRequest
import com.alperburaak.restapp.data.remote.model.authModell.RegisterRequest
import com.alperburaak.restapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository,
    private val tokenStore: TokenDataStore
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init { observeToken() }

    private fun observeToken() {
        viewModelScope.launch {
            tokenStore.tokenFlow.collect { token ->
                _authState.value = if (token.isNullOrBlank()) {
                    AuthState.Unauthenticated
                } else {
                    AuthState.Authenticated(token)
                }
            }
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading()

        viewModelScope.launch {
            repo.login(LoginRequest(email, password))
                .onSuccess { res ->
                    tokenStore.saveToken(res.token)

                }
                .onFailure { e ->
                    _authState.value = AuthState.Loading(error = e.message ?: "Unknown error")
                }
        }
    }

    fun register(request: RegisterRequest) {
        _authState.value = AuthState.Loading()

        viewModelScope.launch {
            repo.register(request)
                .onSuccess { res ->
                    _authState.value = AuthState.Authenticated(res.token)
                }
                .onFailure { e ->
                    _authState.value = AuthState.Loading(error = e.message ?: "Unknown error")
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenStore.clearToken()
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun clearError() {
        // hata saklama şekline göre:
        val current = _authState.value
        if (current is AuthState.Loading && current.error != null) {
            _authState.value = AuthState.Loading(error = null)
        }
    }
}
