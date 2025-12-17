package com.alperburaak.restapp.data.remote.api


import com.alperburaak.restapp.data.remote.model.GetRestaurantResponse
import com.alperburaak.restapp.data.remote.model.restModel.CreateRestaurantRequest
import com.alperburaak.restapp.data.remote.model.restModel.CreateRestaurantResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RestaurantApi {

    @POST("v1/customer/restaurant")
    suspend fun createRestaurant(
        @Body request: CreateRestaurantRequest
    ): Response<CreateRestaurantResponse>

    @GET("v1/customer/restaurant")
    suspend fun getRestaurant(): Response<GetRestaurantResponse>
}
