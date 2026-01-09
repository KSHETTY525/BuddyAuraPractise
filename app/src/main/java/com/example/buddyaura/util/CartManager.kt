package com.example.buddyaura.util

import android.content.Context
import android.content.Intent
import com.example.buddyaura.data.CartItem

object CartManager {

    private val cartItems = mutableListOf<CartItem>()

    fun addItem(context: Context, newItem: CartItem) {

        // 🔍 Check if item already exists
        val existingItem = cartItems.find { it.name == newItem.name }

        if (existingItem != null) {
            // ✅ Item exists → increase quantity
            existingItem.quantity += newItem.quantity
        } else {
            // 🆕 New item → add to cart
            cartItems.add(newItem)
        }
    }

    fun removeSelectedItems(context: Context) {
        cartItems.removeAll { it.isSelected }
        sendCartUpdate(context)
    }

    fun getItems(): List<CartItem> = cartItems

    fun getItemCount(): Int = cartItems.size

    fun getTotalPrice(): Int {
        var total = 0
        for (item in cartItems) {
            total += item.price * item.quantity
        }
        return total
    }

    fun getSelectedTotalPrice(): Int {
        var total = 0
        for (item in cartItems) {
            if (item.isSelected) {
                total += item.price * item.quantity
            }
        }
        return total
    }

    private fun sendCartUpdate(context: Context) {
        val intent = Intent(BroadcastActions.CART_UPDATED)
        intent.putExtra("cartCount", cartItems.size)
        context.sendBroadcast(intent)
    }
}
