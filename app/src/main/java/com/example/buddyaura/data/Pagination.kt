package com.example.buddyaura.data

data class Pagination(
    val currentPage: Int,
    val totalPages: Int,
    val totalCount: Int,
    val limit: Int,
    val hasNextPage: Boolean
)

