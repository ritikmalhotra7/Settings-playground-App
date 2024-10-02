package com.example.settings_playground.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.settings_playground.R
import com.example.settings_playground.databinding.FragmentContentProviderBinding
import com.example.settings_playground.databinding.FragmentFlowsBinding

class ContentProviderFragment : Fragment() {

    private var _binding: FragmentContentProviderBinding? = null
    private val binding: FragmentContentProviderBinding
        get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContentProviderBinding.inflate(inflater)
        // Inflate the layout for this fragment
        setViews()
        return binding.root
    }

    private fun setViews() {
        binding.apply {

        }
    }
}