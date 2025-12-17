package com.alperburaak.restapp.data.remote.model.orderModel



data class UpdateOrderStatusRequest(
    val order_unique_code: String,
    val status: String
)
