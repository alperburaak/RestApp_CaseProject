package com.alperburaak.restapp.data.remote.model.OrderModel


data class UpdateOrderStatusResponse(
    val success: Boolean,
    val message: String?,
    val data: Order?
)
