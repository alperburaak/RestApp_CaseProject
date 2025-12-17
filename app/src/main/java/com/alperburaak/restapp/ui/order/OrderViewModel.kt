package com.alperburaak.restapp.ui.order



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alperburaak.restapp.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderViewModel(
    private val repo: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrderUiState())
    val state: StateFlow<OrderUiState> = _state.asStateFlow()

    fun getOrderList() {
        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            repo.getOrderList()
                .onSuccess { res ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            orders = res.data
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
                }
        }
    }
}
