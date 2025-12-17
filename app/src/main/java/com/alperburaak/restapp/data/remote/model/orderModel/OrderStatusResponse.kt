package com.alperburaak.restapp.data.remote.model.orderModel


data class UpdateOrderStatusResponse(
    val success: Boolean,
    val message: String?,
    val data: Order?
)
