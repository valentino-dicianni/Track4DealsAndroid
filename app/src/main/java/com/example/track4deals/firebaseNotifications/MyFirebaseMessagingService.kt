package com.example.track4deals.firebaseNotifications

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.example.track4deals.MainActivity
import com.example.track4deals.R
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.services.AuthService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance


class MyFirebaseMessagingService() : FirebaseMessagingService(), KodeinAware {
    override val kodein by closestKodein()
    private val authService: AuthService by instance()
    private val userProvider: UserProvider by instance()

    override fun onNewToken(token: String) {
        Log.d("MyFirebaseMessagingService", "Refreshed token: $token")
        if (userProvider.isLoggedIn())
            registerToken(token)
    }

    private fun registerToken(token: String) {
        GlobalScope.launch(Dispatchers.IO) {
            authService.registerFirebaseTokenAsync(token).await()
            Log.d("MyFirebaseMessagingService", "Sent token to Track4Deals Server")
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("MyFirebaseMessagingService", "From: ${remoteMessage.from}")

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("MyFirebaseMessagingService", "Message Notification Body: ${it.body}")
        }
        remoteMessage.notification?.let {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

            val isNotificationEnabled = sharedPref.getBoolean(
                getString(R.string.notification_preference), true
            )

            if (isNotificationEnabled) sendNotification(it)
        }
    }

    private fun sendNotification(message: RemoteMessage.Notification) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val notificationBuilder = NotificationCompat.Builder(this, "Channel")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(message.title.toString())
            .setContentText(message.body.toString())
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Channel",
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

}