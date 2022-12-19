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
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.settings_playground.R
import com.example.settings_playground.databinding.FragmentTimerBinding
import com.example.settings_playground.utils.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.prefs.Preferences
import kotlin.math.roundToInt
const val USER_PREFERENCES_NAME = "USER_PREFERENCES_NAME"
const val DATA_STORE_KEY_FOR_TIMER = "DATA_STORE_KEY_FOR_TIMER"
@AndroidEntryPoint
class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding: FragmentTimerBinding get() = _binding!!
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    companion object{
        private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)
    }

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
        val minutes = resultInt % 86400 / 60 % 60
        val seconds = resultInt % 86400 % 60
        return String.format("%02d:%02d:%02d",hours,minutes,seconds)
    }

    private fun setViews() {
        binding.apply {
            fragmentTimerBtStart.setOnClickListener { startStopTimer() }
            fragmentTimerBtReset.setOnClickListener { resetTimer() }
            lifecycleScope.launch {
                time = readTimer()?.let {
                    fragmentTimerTvTimer.text = getTimeFromDouble(it)
                    it
                } ?: time
            }
        }
    }

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.fragmentTimerTvTimer.text = getTimeFromDouble(time)
        lifecycleScope.launch{
            saveTimer(time)
        }
    }

    private fun startStopTimer() {
        if(timerStarted) stopTimer()
        else startTimer()
    }

    private fun stopTimer() {
        requireActivity().stopService(serviceIntent)
        binding.fragmentTimerBtStart.text = getString(R.string.start_timer)
        timerStarted = false
        lifecycleScope.launch{
            saveTimer(time)
        }
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA,time)
        requireActivity().startService(serviceIntent)
        binding.fragmentTimerBtStart.text = getString(R.string.stop_timer)
        timerStarted = true
    }

    private suspend fun saveTimer(timerValue:Double){
        val dataStoreKey = doublePreferencesKey(DATA_STORE_KEY_FOR_TIMER)
        requireActivity().dataStore.edit { settings ->
            settings[dataStoreKey] = timerValue
        }
    }
    private suspend fun readTimer():Double?{
        val dataStoreKey = doublePreferencesKey(DATA_STORE_KEY_FOR_TIMER)
        val preferences = requireActivity().dataStore.data.first()
        return preferences[dataStoreKey]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().unregisterReceiver(updateTime)
    }
}