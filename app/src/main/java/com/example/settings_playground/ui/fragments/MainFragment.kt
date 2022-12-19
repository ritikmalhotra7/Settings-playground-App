package com.example.settings_playground.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.settings_playground.R
import com.example.settings_playground.databinding.FragmentMainBinding
import com.example.settings_playground.databinding.FragmentOTPServiceBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater)
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            fragmentMainBtOtpService.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_OTPServiceFragment) }
            fragmentMainBtTimer.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_timerFragment) }
            fragmentMainBtScheduleNotification.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_scheduleNotificationFragment) }
            fragmentFlowsBtFlow.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_flowsFragment) }
        }
    }

}