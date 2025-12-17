package com.alperburaak.restapp.data.remote.model.WsModel

data class WsOrderCreatedPayload(
    val order_id: Int,
    val unique_code: String,
    val customer_name: String,
    val customer_phone: String,
    val customer_email: String,
    val delivery_address: WsDeliveryAddress,
    val order_details: WsOrderDetails,
    val items: List<WsOrderItem>,
    val created_at: String
)

data class WsDeliveryAddress(
    val full_address: String,
    val city: String,
    val district: String,
    val neighborhood: String
)

data class WsOrderDetails(
    val total_amount: Double,
    val discount_amount: Double,
    val delivery_fee: Double,
    val final_amount: Double,
    val payment_method: String,
    val payment_status: String,
    val status: String,
    val note: String?
)

data class WsOrderItem(
    val product_name: String,
    val quantity: Int,
    val price: Double,
    val total: Double
)