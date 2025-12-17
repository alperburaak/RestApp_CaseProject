package com.alperburaak.restapp.data.remote.model.orderModel




data class OrderCreatedEvent(
    val order_id: Int? = null,
    val unique_code: String? = null,
    val customer_name: String? = null,
    val customer_phone: String? = null,
    val customer_email: String? = null,
    val delivery_address: OrderAddressEvent? = null,
    val order_details: OrderDetails? = null,
    val items: List<OrderItem>? = null,
    val created_at: String?= null
)

data class OrderAddressEvent(
    val full_address: String? = null,
    val city: String? = null,
    val district: String? = null,
    val neighborhood: String? = null,
    val latitude: String? = null,
    val longitude: String? = null
)
