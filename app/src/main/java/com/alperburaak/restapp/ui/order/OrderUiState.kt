package com.alperburaak.restapp.ui.order

import com.alperburaak.restapp.data.remote.model.orderModel.Order


data class OrderUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val orders: List<Order> = emptyList(),
    val liveEvents: List<String> = emptyList(),
    val liveOrderBanner: Order? = null

)
