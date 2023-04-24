package com.example.settings_playground.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.settings_playground.R
import com.example.settings_playground.ui.activities.MainActivity

const val notificationID = 2
const val channelID = "channelIDs"
const val ACTION_OPEN_FRAGMENT = "ACTION_OPEN_FRAGMENT"
class NotificationWorker(context: Context, workerParams:WorkerParameters):Worker(context,workerParams){
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        showNotification()
        return Result.Success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification() {
        val intent = Intent(applicationContext,MainActivity::class.java).apply{
            action = ACTION_OPEN_FRAGMENT
        }
        createNotificationChannel()
        val pendingIntent = PendingIntent.getActivity(applicationContext,0,intent,PendingIntent.FLAG_MUTABLE)
        val notification = NotificationCompat.Builder(applicationContext, channelID).apply {
            setContentTitle("yes work manager is working")
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setAutoCancel(true)
            setContentIntent(pendingIntent)
        }.build()
        NotificationManagerCompat.from(applicationContext).notify(notificationID,notification)
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                "My Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "This is my notification channel"
            }

            // Register the notification channel with the system
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}