package com.example.buddyaura.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.buddyaura.util.NotificationHelper

class OfferAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.showOfferNotification(context)
    }
}
