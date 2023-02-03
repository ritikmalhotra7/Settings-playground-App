package com.example.settings_playground.ui.fragments

import android.app.*
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.settings_playground.databinding.FragmentScheduleNotificationBinding
import com.example.settings_playground.ui.receivers.*
import com.example.settings_playground.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ScheduleNotificationFragment : Fragment() {

    private var _binding: FragmentScheduleNotificationBinding? = null
    private val binding: FragmentScheduleNotificationBinding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleNotificationBinding.inflate(inflater)
        createNotificationChannel()
        setViews()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setViews() {
        binding.apply {
            fragmentScheduleNotificationBtScheduleNotification.setOnClickListener { notifyNotification() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun notifyNotification() {
        val title = binding.fragmentScheduleNotificationEtTitle.text.toString()
        val message = binding.fragmentScheduleNotificationEtMessage.text.toString()
        val intent = Intent(requireActivity().applicationContext, NotificationService::class.java)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity().applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = getTime()
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = DateFormat.getLongDateFormat(requireActivity().applicationContext)
        val timeFormat = DateFormat.getTimeFormat(requireActivity().applicationContext)

        AlertDialog.Builder(requireActivity())
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getTime(): Long {
        val min = binding.fragmentScheduleNotificationTpTimePicker.minute
        val hour = binding.fragmentScheduleNotificationTpTimePicker.hour
        val day = binding.fragmentScheduleNotificationDtDatePicker.dayOfMonth
        val month = binding.fragmentScheduleNotificationDtDatePicker.month
        val year = binding.fragmentScheduleNotificationDtDatePicker.year
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, min)
        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val channel =
            NotificationChannel(channelID, "notify", NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "A description is also important"
        val notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}