package com.example.settings_playground.ui.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ServicePlayground:Service() {
    override fun onBind(p0: Intent?) = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }
}
