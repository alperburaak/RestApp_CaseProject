package com.alperburaak.restapp.data.repository



import com.alperburaak.restapp.data.remote.api.OrderApi
import com.alperburaak.restapp.data.remote.model.orderModel.GetOrderListResponse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(
    private val api: OrderApi
) : OrderRepository {

    override suspend fun getOrderList(): Result<GetOrderListResponse> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.getOrderList()
                if (response.isSuccessful) {
                    response.body() ?: throw IllegalStateException("Empty response body")
                } else {
                    throw IllegalStateException("HTTP ${response.code()} - ${response.message()}")
                }
            }
        }
}
