package com.example.settings_playground.ui.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BroadcastReceiversPlayground(): BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("taget","started receiver")
        val intent = Intent().apply {

        }
    }
}