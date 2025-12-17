package com.alperburaak.restapp.data.remote.model.wsModel



import com.alperburaak.restapp.data.remote.model.orderModel.OrderAddress
import com.alperburaak.restapp.data.remote.model.orderModel.OrderDetails
import com.alperburaak.restapp.data.remote.model.orderModel.OrderItem


data class OrderCreatedEvent(
    val order_id: Int,
    val unique_code: String,
    val customer_name: String,
    val customer_phone: String,
    val customer_email: String,
    val delivery_address: OrderAddress,
    val order_details: OrderDetails,
    val items: List<OrderItem>,
    val created_at: String
)
