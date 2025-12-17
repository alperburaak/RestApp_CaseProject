package com.alperburaak.restapp.data.remote.model.authModel

data class User(
    val id: Int,
    val restaurant_id: Int,
    val name: String,
    val email: String,
    val bussinessName: String,
    val bussinessPhone: String,
    val bussinessEmail: String,
    val bussinessAddress: String,
    val bussinessCity: String,
    val bussinessPostalCode: String,
    val bussinessCountry: String,
    val bussinessLogo: String,
    val bussinessDescription: String,
    val isCustomer: Boolean,
    val isSubCustomer: Boolean,
    val isAdmin: Boolean
)