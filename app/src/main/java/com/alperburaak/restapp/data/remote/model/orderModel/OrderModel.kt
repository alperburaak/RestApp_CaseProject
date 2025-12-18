package com.alperburaak.restapp.data.remote.model.orderModel

data class Order(
    val order_id: Int,
    val restaurant_id: Int?,
    val unique_code: String,
    val customer: OrderCustomer,
    val delivery_address: OrderAddress,
    val order_details: OrderDetails,
    val items: List<OrderItem>,
    val created_at: String
)



data class OrderCustomer(
    val name: String,
    val phone: String,
    val email: String
)

data class OrderAddress(
    val full_address: String,
    val city: String,
    val district: String,
    val neighborhood: String,
    val latitude: Double,
    val longitude: Double
)

data class OrderDetails(
    val total_amount: Double,
    val discount_amount: Double,
    val delivery_fee: Double,
    val final_amount: Double,
    val payment_method: String,
    val payment_status: String,
    val status: String,
    val note: String?
)

data class OrderItem(
    val product_name: String,
    val quantity: Int,
    val price: Double,
    val total: Double
)
