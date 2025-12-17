package com.alperburaak.restapp.data.remote.model.OrderModel



data class UpdateOrderStatusRequest(
    val order_unique_code: String,
    val status: String
)
