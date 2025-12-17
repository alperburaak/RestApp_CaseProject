package com.alperburaak.restapp.data.remote.model.OrderModel


data class GetOrderListResponse(
    val success: Boolean,
    val message: String,
    val data: List<Order>
)
