package com.example.buddyaura.util

import com.example.buddyaura.data.Catalogue

object WishlistManager {

    private val wishlist = mutableListOf<Catalogue>()

    fun add(item: Catalogue) {
        if (!wishlist.any { it.title == item.title }) {
            wishlist.add(item)
        }
    }

    fun remove(item: Catalogue) {
        wishlist.removeAll { it.title == item.title }
    }

    fun isWishlisted(item: Catalogue): Boolean {
        return wishlist.any { it.title == item.title }
    }

    fun getWishlist(): List<Catalogue> = wishlist
}
