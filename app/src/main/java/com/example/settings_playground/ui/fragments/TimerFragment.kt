package com.example.settings_playground.ui.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.settings_playground.R
import com.example.settings_playground.databinding.FragmentTimerBinding
import com.example.settings_playground.utils.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlin.math.roundToInt

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding: FragmentTimerBinding get() = _binding!!
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimerBinding.inflate(inflater)
        setViews()
        serviceIntent = Intent(requireActivity().applicationContext, TimerService::class.java)
        requireActivity().registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
        return binding.root
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            binding.fragmentTimerTvTimer.text = getTimeFromDouble(time)
        }
    }

    private fun getTimeFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 / 60
        val seconds = resultInt % 86400
        return String.format("%02d:%02d:%02d",hours,minutes,seconds)
    }

    private fun setViews() {
        binding.apply {
            fragmentTimerBtStart.setOnClickListener { startStopTimer() }
            fragmentTimerBtReset.setOnClickListener { resetTimer() }
        }
    }

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.fragmentTimerTvTimer.text = getTimeFromDouble(time)
    }

    private fun startStopTimer() {
        if(timerStarted) stopTimer()
        else startTimer()
    }

    private fun stopTimer() {
        requireActivity().stopService(serviceIntent)
        binding.fragmentTimerBtStart.text = getString(R.string.start_timer)
        timerStarted = false
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA,time)
        requireActivity().startService(serviceIntent)
        binding.fragmentTimerBtStart.text = getString(R.string.stop_timer)
        timerStarted = true
    }

}