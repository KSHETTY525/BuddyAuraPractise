package com.example.buddyaura.data

data class ApiCatalogue(
    val id: Int,
    val catalogueName: String,
    val description: String?,
    val minPrice: Int,
    val catalogueImage: List<String>
)

