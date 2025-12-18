package com.alperburaak.restapp.ui.rest



import com.alperburaak.restapp.data.remote.model.Restaurant

data class RestaurantUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val restaurants: List<Restaurant> = emptyList(),
    val selectedRestaurant: Restaurant? = null,
    val created: Boolean = false
)
