package com.alperburaak.restapp.data.remote.model

data class Restaurant(
    val id: Int,
    val name: String,
    val slug: String,
    val email: String,
    val phone: String,

    val physical_address: String,
    val city: String,
    val district: String,
    val neighborhood: String,

    val city_id: String,
    val district_id: String,
    val neighborhood_id: Int,

    val postal_code: String,
    val country: String,
    val logo: String,
    val video_url: String,

    val main_language: String,
    val support_menu_lnaguage_ids: String,

    val operation_start_time: String,
    val operation_end_time: String,

    val description: String,
    val google_maps_url: String,
    val qr_code: String
)

