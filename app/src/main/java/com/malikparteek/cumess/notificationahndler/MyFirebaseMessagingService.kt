package com.malikparteek.cumess.notificationahndler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.malikparteek.cumess.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent
import com.malikparteek.cumess.MainActivity
import android.app.PendingIntent
import android.media.RingtoneManager
import android.media.AudioAttributes



class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "No title"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "No body"

        showNotification(title, body)
    }


    private fun showNotification(title: String, message: String) {
        val channelId = "default_channel"

        // Intent to launch MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Make sure this icon exists
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // 👈 Open app on tap
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }
            notificationManager.createNotificationChannel(channel)
        }


    }
}
