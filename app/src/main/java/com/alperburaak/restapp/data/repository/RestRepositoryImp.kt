package com.alperburaak.restapp.data.repository

import com.alperburaak.restapp.data.remote.api.RestaurantApi
import com.alperburaak.restapp.data.remote.model.GetRestaurantResponse
import com.alperburaak.restapp.data.remote.model.restModel.CreateRestaurantRequest
import com.alperburaak.restapp.data.remote.model.restModel.CreateRestaurantResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RestaurantRepositoryImpl(
    private val api: RestaurantApi
) : RestaurantRepository {

    override suspend fun createRestaurant(request: CreateRestaurantRequest): Result<CreateRestaurantResponse> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.createRestaurant(request)
                if (response.isSuccessful) {
                    response.body() ?: throw IllegalStateException("Empty response body")
                } else {
                    throw IllegalStateException("HTTP ${response.code()} - ${response.message()}")
                }
            }
        }

    override suspend fun getRestaurant(): Result<GetRestaurantResponse> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = api.getRestaurant()
                if (response.isSuccessful) {
                    response.body() ?: throw IllegalStateException("Empty response body")
                } else {
                    throw IllegalStateException("HTTP ${response.code()} - ${response.message()}")
                }
            }
        }
}
