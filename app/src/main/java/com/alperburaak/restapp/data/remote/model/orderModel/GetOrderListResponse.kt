package com.alperburaak.restapp.data.remote.model.orderModel


data class GetOrderListResponse(
    val success: Boolean,
    val message: String,
    val data: List<Order>
)
