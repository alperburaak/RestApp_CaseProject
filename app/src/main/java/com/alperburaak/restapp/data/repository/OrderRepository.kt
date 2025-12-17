package com.alperburaak.restapp.data.repository

import com.alperburaak.restapp.data.remote.model.orderModel.GetOrderListResponse


interface OrderRepository {
    suspend fun getOrderList(): Result<GetOrderListResponse>
}
