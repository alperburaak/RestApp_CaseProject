package com.alperburaak.restapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperburaak.restapp.data.local.TokenDataStore
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

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        observeToken()
    }

    private fun observeToken() {
        viewModelScope.launch {
            tokenStore.tokenFlow.collect { token ->
                if (token != null) {
                    _state.update { it.copy(token = token) }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = repo.login(LoginRequest(email, password))

            result
                .onSuccess { res ->
                    _state.update { it.copy(isLoading = false, token = res.token) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                }
        }
    }

    fun register(request: RegisterRequest) {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = repo.register(request)

            result
                .onSuccess { res ->
                    val token = res.token
                    _state.update { it.copy(isLoading = false, token = token) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
