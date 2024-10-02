package com.example.settings_playground.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.settings_playground.R
import com.example.settings_playground.adapter.ConfigurationAdapter
import com.example.settings_playground.databinding.FragmentMVVMConfigurationChangesBinding
import com.example.settings_playground.ui.viewmodels.ConfigurationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MVVMConfigurationChangesFragment : Fragment() {
    private var _binding : FragmentMVVMConfigurationChangesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ConfigurationAdapter

    private val viewModel : ConfigurationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMVVMConfigurationChangesBinding.inflate(inflater)
        setViews()
        return binding.root
    }

    private fun setViews() {
        adapter = ConfigurationAdapter().apply {
            setTextChangedListener {
                viewModel.onTextChanged(it)
            }
        }
        binding.apply {
            lifecycleScope.launch{
                repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.onConfigurationChange()
                    viewModel.list.collectLatest {
                        Log.d("list",it.toString())
                        adapter.setList(it)
                    }
                }
            }
            rv.apply {
                adapter = this@MVVMConfigurationChangesFragment.adapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            btn.setOnClickListener { viewModel.apiCall() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}