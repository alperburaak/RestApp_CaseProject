package com.alperburaak.restapp.di


import com.alperburaak.restapp.data.remote.api.AuthApi
import com.alperburaak.restapp.data.repository.AuthRepository
import com.alperburaak.restapp.data.repository.AuthRepositoryImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.alperburaak.restapp.ui.auth.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import com.alperburaak.restapp.data.local.TokenDataStore
import com.alperburaak.restapp.data.remote.AuthInterceptor
import com.alperburaak.restapp.data.remote.api.OrderApi
import com.alperburaak.restapp.data.remote.api.RestaurantApi
import com.alperburaak.restapp.data.remote.ws.PusherManager
import com.alperburaak.restapp.data.repository.OrderRepository
import com.alperburaak.restapp.data.repository.OrderRepositoryImpl
import com.alperburaak.restapp.data.repository.RestaurantRepository
import com.alperburaak.restapp.data.repository.RestaurantRepositoryImpl
import com.alperburaak.restapp.ui.order.OrderViewModel
import com.alperburaak.restapp.ui.rest.RestaurantViewModel


private const val BASE_URL = "http://188.34.155.223/new-qr-menu/api/"

val appModule = module {

    // 1. AuthInterceptor (Token ekleyici)
    single { AuthInterceptor(tokenStore = get()) }

    // 2. OkHttpClient
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>()) // Token interceptor'ı EKLE
            .addInterceptor(logging)                // Log interceptor'ı EKLE
            .build()
    }

    // 3. Retrofit
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Tanımları
    single<AuthApi> { get<Retrofit>().create(AuthApi::class.java) }
    single<RestaurantApi> { get<Retrofit>().create(RestaurantApi::class.java) }
    single<OrderApi> { get<Retrofit>().create(OrderApi::class.java) }

    // Repository Tanımları
    single<AuthRepository> { AuthRepositoryImpl(api = get(), tokenStore = get()) }
    single<RestaurantRepository> { RestaurantRepositoryImpl(api = get()) }
    single<OrderRepository> { OrderRepositoryImpl(api = get()) }

    // ViewModel Tanımları
    viewModel { AuthViewModel(repo = get(), tokenStore = get()) }
    viewModel { RestaurantViewModel(repo = get()) }
    viewModel { OrderViewModel(repo = get(), pusherManager = get()) }

    // Diğerleri
    single { TokenDataStore(get()) }
    single { PusherManager(tokenStore = get()) }
}



