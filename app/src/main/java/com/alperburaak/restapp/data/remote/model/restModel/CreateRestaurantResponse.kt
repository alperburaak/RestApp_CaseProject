package com.alperburaak.restapp.data.remote.model.restModel

import com.alperburaak.restapp.data.remote.model.Restaurant


data class CreateRestaurantResponse(
    val success: Boolean,
    val message: String?,
    val restaurant: Restaurant?,
    val limit_info: List<Any>
)
