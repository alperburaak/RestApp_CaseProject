package com.alperburaak.restapp.data.repository


import com.alperburaak.restapp.data.remote.model.GetRestaurantResponse
import com.alperburaak.restapp.data.remote.model.restModel.CreateRestaurantRequest
import com.alperburaak.restapp.data.remote.model.restModel.CreateRestaurantResponse

interface RestaurantRepository {
    suspend fun createRestaurant(request: CreateRestaurantRequest): Result<CreateRestaurantResponse>
    suspend fun getRestaurant(): Result<GetRestaurantResponse>
}
