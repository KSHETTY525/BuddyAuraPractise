package com.example.buddyaura.data

data class Catalogue(
    val title: String,
    val description: String,
    val price: Int,
    val imageUrl: String? = null,
    val imageRes: Int? = null
)

