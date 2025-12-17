package com.alperburaak.restapp.ui.rest



import com.alperburaak.restapp.data.remote.model.Restaurant

data class RestaurantUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val restaurant: Restaurant? = null,
    val created: Boolean = false
)
