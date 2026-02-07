package com.example.buddyaura.data

data class CartItem(
    val name: String,
    val description: String,
    val price: Int,
    var quantity: Int,
    val imageRes: Int,
    var isSelected: Boolean = false   // <-- add this line
)
