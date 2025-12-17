package com.alperburaak.restapp.data.remote.model.OrderModel

data class OrderDetailResponse(
    val success: Boolean,
    val message: String?,
    val data: Order?
)
