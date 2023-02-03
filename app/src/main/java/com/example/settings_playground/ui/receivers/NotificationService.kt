package com.example.settings_playground.ui.receivers

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.settings_playground.R

const val notificationID = 1
const val channelID = "channelID"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class NotificationService : BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent) {
        val notification: Notification = NotificationCompat.Builder(p0, channelID)
            .setContentTitle(p1.extras!!.getString(titleExtra))
            .setContentText(p1.extras!!.getString(messageExtra))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        val notificationManager =
            p0.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationID, notification)
    }
}