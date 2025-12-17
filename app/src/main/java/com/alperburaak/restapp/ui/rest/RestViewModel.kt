package com.alperburaak.restapp.ui.rest



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperburaak.restapp.data.remote.model.Restaurant
import com.alperburaak.restapp.data.remote.model.restModel.CreateRestaurantRequest
import com.alperburaak.restapp.data.repository.RestaurantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RestaurantViewModel(
    private val repo: RestaurantRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RestaurantUiState())
    val state: StateFlow<RestaurantUiState> = _state.asStateFlow()

    fun getRestaurant() {
        _state.update { it.copy(isLoading = true, error = null, created = false) }

        viewModelScope.launch {
            repo.getRestaurant()
                .onSuccess { res ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            restaurant = res.data[0],
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                }
        }
    }

    fun createRestaurant(request: CreateRestaurantRequest) {
        _state.update { it.copy(isLoading = true, error = null, created = false) }

        viewModelScope.launch {
            repo.createRestaurant(request)
                .onSuccess { res ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            restaurant = res.restaurant,
                            created = true,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun clearCreatedFlag() {
        _state.update { it.copy(created = false) }
    }
}
