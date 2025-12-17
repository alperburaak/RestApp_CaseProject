package com.alperburaak.restapp.data.remote.api




import com.alperburaak.restapp.data.remote.model.orderModel.GetOrderListResponse
import retrofit2.Response
import retrofit2.http.GET

interface OrderApi {

    @GET("v1/order/get_order_list")
    suspend fun getOrderList(): Response<GetOrderListResponse>
}
