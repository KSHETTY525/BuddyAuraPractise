package com.example.buddyaura.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NetworkReceiver : BroadcastReceiver() {

    private var isFirstTime = true

    override fun onReceive(context: Context, intent: Intent) {

        if (isFirstTime) {
            isFirstTime = false
            return
        }

        Toast.makeText(context, "Network status changed", Toast.LENGTH_SHORT).show()
    }
}
