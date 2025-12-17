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
import com.alperburaak.restapp.data.remote.ws.SocketManager
import com.alperburaak.restapp.data.repository.OrderRepository
import com.alperburaak.restapp.data.repository.OrderRepositoryImpl
import com.alperburaak.restapp.data.repository.RestaurantRepository
import com.alperburaak.restapp.data.repository.RestaurantRepositoryImpl
import com.alperburaak.restapp.ui.order.OrderViewModel
import com.alperburaak.restapp.ui.rest.RestaurantViewModel


private const val BASE_URL = "http://188.34.155.223/new-qr-menu/api/"

val appModule = module {

    // OkHttp
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Api
    single<AuthApi> {
        get<Retrofit>().create(AuthApi::class.java)
    }

    // Repository
    single<AuthRepository> {
        AuthRepositoryImpl(api = get(), tokenStore = get())
    }

    viewModel { AuthViewModel(repo = get(),tokenStore = get()) }

    single { TokenDataStore(get()) }

    single<RestaurantApi> { get<retrofit2.Retrofit>().create(RestaurantApi::class.java) }

    single<RestaurantRepository> {
        RestaurantRepositoryImpl(api = get())
    }

    viewModel { RestaurantViewModel(repo = get()) }

    single { AuthInterceptor(tokenStore = get()) }

    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())   // önce header eklesin
            .addInterceptor(logging)                  // sonra loglasın
            .build()
    }

    single<OrderApi> { get<retrofit2.Retrofit>().create(OrderApi::class.java) }

    single<OrderRepository> {
        OrderRepositoryImpl(api = get())
    }

    viewModel { OrderViewModel(repo = get(),pusherManager = get()) }

    single { SocketManager() }

    single { PusherManager(tokenStore = get()) }







}
