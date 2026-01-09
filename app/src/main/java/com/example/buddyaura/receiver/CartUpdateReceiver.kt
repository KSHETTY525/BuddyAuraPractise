package com.example.buddyaura.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CartUpdateReceiver(
    private val onCartUpdated: (Int) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val count = intent.getIntExtra("cartCount", 0)
        onCartUpdated(count)
    }


}
