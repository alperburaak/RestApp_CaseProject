package com.alperburaak.restapp.data.remote.model.orderModel



data class AcceptOrCancelOrderRequest(
    val order_unique_code: String,
    val status: String // "accepted" | "rejected"
)
