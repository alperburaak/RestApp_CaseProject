package com.alperburaak.restapp.data.remote.model.orderModel



data class AcceptOrCancelOrderResponse(
    val success: Boolean,
    val message: String,
    val data: Any? = null
)
