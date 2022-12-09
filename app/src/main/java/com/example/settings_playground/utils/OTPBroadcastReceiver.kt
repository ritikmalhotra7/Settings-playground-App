package com.example.settings_playground.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class OTPBroadcastReceiver : BroadcastReceiver() {
    var otpListener: OTPListener? = null
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == p1?.action) {
            val extras = p1.extras
            val otpRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            when (otpRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val messageIntent =
                        extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    otpListener?.onSuccess(messageIntent)
                }
                CommonStatusCodes.TIMEOUT -> {
                    otpListener?.onFailure()
                }
            }
        }
    }

    interface OTPListener {
        fun onSuccess(intent: Intent?)
        fun onFailure()
    }
}