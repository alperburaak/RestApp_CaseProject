package com.alperburaak.restapp.data.remote.api




import com.alperburaak.restapp.data.remote.model.orderModel.AcceptOrCancelOrderRequest
import com.alperburaak.restapp.data.remote.model.orderModel.AcceptOrCancelOrderResponse
import com.alperburaak.restapp.data.remote.model.orderModel.GetOrderListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OrderApi {

    @GET("v1/order/get_order_list")
    suspend fun getOrderList(): Response<GetOrderListResponse>

    @POST("v1/order/accept_or_cancel_order")
    suspend fun acceptOrCancelOrder(
        @Body request: AcceptOrCancelOrderRequest
    ): Response<AcceptOrCancelOrderResponse>
}


