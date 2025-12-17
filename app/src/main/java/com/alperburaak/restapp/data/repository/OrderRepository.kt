package com.alperburaak.restapp.data.repository

import com.alperburaak.restapp.data.remote.model.orderModel.AcceptOrCancelOrderResponse
import com.alperburaak.restapp.data.remote.model.orderModel.GetOrderListResponse


interface OrderRepository {
    suspend fun getOrderList(): Result<GetOrderListResponse>

    suspend fun acceptOrder(orderUniqueCode: String): Result<AcceptOrCancelOrderResponse>

    suspend fun rejectOrder(orderUniqueCode: String): Result<AcceptOrCancelOrderResponse>

}
