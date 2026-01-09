package com.example.buddyaura.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.buddyaura.R

object NotificationHelper {

    fun showOfferNotification(context: Context) {
        val channelId = "offer_channel"

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Offers",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.outline_notifications_active_24)
            .setContentTitle("BuddyAura Offers 🎉")
            .setContentText("Check out today's exciting deals!")
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }
}
