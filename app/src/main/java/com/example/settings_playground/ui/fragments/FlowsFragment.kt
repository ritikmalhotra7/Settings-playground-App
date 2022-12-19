package com.example.settings_playground.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.settings_playground.adapter.NewsAdapter
import com.example.settings_playground.utils.Resources
import com.example.settings_playground.databinding.FragmentFlowsBinding
import com.example.settings_playground.ui.viewmodels.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FlowsFragment : Fragment() {

    private lateinit var rvAdapter: NewsAdapter
    private var _binding: FragmentFlowsBinding? = null
    private val binding: FragmentFlowsBinding
        get() = _binding!!
    private var dataList: ArrayList<String> = arrayListOf()
    private val viewModel: NetworkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFlowsBinding.inflate(inflater)
        setViews(dataList)
        return binding.root
    }

    private fun setViews(dataList: ArrayList<String>) {
        /**
         * These all are observers which serves different purposes and have different use cases
         * Live Data -> It is a observer for data collected/ handle configuration changes have states to handle.
         * State Flow -> It have same functionality as live data, it also have states to handle but it will emit whenever there is a configuration change eg: having a toast on screen and...
         * ...whenever your configuration is changed then the observer will send this as a new data and repeat itself, it also need coroutine to launch.
         * Flow: It doesn't have support for state as a result it didn't survive configuration changes we use flow whenever we have a process to do it will do its job and...
         * ...after configuration change the job is resetted.
         * Shared Flow -> It is most similar to live data, serves same function as live data, it will not emit if there is a configuration change so one time job but survive configuration changes
         */
        binding.apply {
            fragmentFlowsBtLiveData.setOnClickListener { viewModel.topHeadlinesToLiveData("US",1) }
            fragmentFlowsBtStateFlow.setOnClickListener { viewModel.topHeadLinesToStateFlow("US",1) }
            fragmentFlowsBtFlow.setOnClickListener { observeFlow() }
            fragmentFlowsBtSharedFlow.setOnClickListener { viewModel.topHeadLinesToSharedFlow("US",1) }
            rvAdapter = NewsAdapter(dataList = dataList)
            setupRecyclerView()
        }
        observeLiveData()
        observeStateFlow()
        observeSharedFlow()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        binding.fragmentFlowsRv.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            rvAdapter.notifyDataSetChanged()
        }
    }

    private fun observeLiveData() {
        viewModel.liveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resources.Success -> {
                    response.data?.let{ newsResponse ->
                        newsResponse.articles!!.forEach {
                            dataList.add(it.title!!)
                        }
                    }
                    Toast.makeText(requireContext(),"from liveData",Toast.LENGTH_SHORT).show()
                    setupRecyclerView()
                }
                is Resources.Error -> {}
                is Resources.Loading -> {}
            }
        }
    }
    private fun observeStateFlow(){
        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collectLatest {response ->
                when (response) {
                    is Resources.Success -> {
                        response.data?.let{ newsResponse ->
                            newsResponse.articles!!.forEach {
                                dataList.add(it.title!!)
                            }
                        }
                        Toast.makeText(requireContext(),"from stateFlow",Toast.LENGTH_SHORT).show()
                        Log.d("taget","stateFlow")
                        setupRecyclerView()
                    }
                    is Resources.Error -> {}
                    is Resources.Loading -> {}
                }
            }
        }
    }
    private fun observeFlow(){
        lifecycleScope.launch {
            viewModel.topHeadLinesToFlow("in",1).collectLatest {response ->
                when (response) {
                    is Resources.Success -> {
                        response.data?.let{ newsResponse ->
                            newsResponse.articles!!.forEach {
                                dataList.add(it.title!!)
                            }
                        }
                        Toast.makeText(requireContext(),"from flow",Toast.LENGTH_SHORT).show()
                        setupRecyclerView()
                    }
                    is Resources.Error -> {}
                    is Resources.Loading -> {}
                }
            }
        }
    }
    private fun observeSharedFlow() {
        lifecycleScope.launch {
            viewModel.sharedFlow.collectLatest {response ->
                when (response) {
                    is Resources.Success -> {
                        response.data?.let{ newsResponse ->
                            newsResponse.articles!!.forEach {
                                dataList.add(it.title!!)
                            }
                        }
                        Toast.makeText(requireContext(),"from sharedFlow",Toast.LENGTH_SHORT).show()
                        setupRecyclerView()
                    }
                    is Resources.Error -> {}
                    is Resources.Loading -> {}
                }
            }
        }
    }
}