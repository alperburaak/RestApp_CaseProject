package com.alperburaak.restapp.data.remote.model.orderModel

data class OrderDetailResponse(
    val success: Boolean,
    val message: String?,
    val data: Order?
)
