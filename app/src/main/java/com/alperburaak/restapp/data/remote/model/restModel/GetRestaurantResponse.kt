package com.alperburaak.restapp.data.remote.model

data class GetRestaurantResponse(
    val success: Boolean,
    val message: String,
    val data: List<Restaurant>,
    val links: PaginationLinks,
    val meta: Meta
)

data class PaginationLinks(
    val first: String,
    val last: String,
    val prev: String?,
    val next: String?
)

data class Meta(
    val currentPage: Int,
    val from: Int?,
    val lastPage: Int,
    val links: List<MetaLink>,
    val path: String,
    val perPage: Int,
    val to: Int?,
    val total: Int
)

data class MetaLink(
    val url: String?,
    val label: String,
    val page: Int?,
    val active: Boolean
)