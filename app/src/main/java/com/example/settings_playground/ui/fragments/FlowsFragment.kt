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
import com.example.settings_playground.databinding.FragmentFlowsBinding
import com.example.settings_playground.ui.viewmodels.NetworkViewModel
import com.example.settings_playground.utils.Resources
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

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
        consumingSharedFlow()
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
            fragmentFlowsBtLiveData.setOnClickListener { viewModel.topHeadlinesToLiveData("US", 1) }
            fragmentFlowsBtStateFlow.setOnClickListener {
                viewModel.topHeadLinesToStateFlow(
                    "US",
                    1
                )
            }
            fragmentFlowsBtFlow.setOnClickListener { observeFlow() }
            fragmentFlowsBtSharedFlow.setOnClickListener {
                viewModel.topHeadLinesToSharedFlow(
                    "US",
                    1
                )
            }
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
                    response.data?.let { newsResponse ->
                        newsResponse.articles!!.forEach {
                            dataList.add(it.title!!)
                        }
                    }
                    Toast.makeText(requireContext(), "from liveData", Toast.LENGTH_SHORT).show()
                    setupRecyclerView()
                }
                is Resources.Error -> {}
                is Resources.Loading -> {}
            }
        }
    }

    private fun observeStateFlow() {
        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collectLatest { response ->
                when (response) {
                    is Resources.Success -> {
                        response.data?.let { newsResponse ->
                            newsResponse.articles!!.forEach {
                                dataList.add(it.title!!)
                            }
                        }
                        Toast.makeText(requireContext(), "from stateFlow", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("taget", "stateFlow")
                        setupRecyclerView()
                    }
                    is Resources.Error -> {}
                    is Resources.Loading -> {}
                }
            }
        }
    }

    private fun observeFlow() {
        lifecycleScope.launch {
            viewModel.topHeadLinesToFlow("in", 1).collectLatest { response ->
                when (response) {
                    is Resources.Success -> {
                        response.data?.let { newsResponse ->
                            newsResponse.articles!!.forEach {
                                dataList.add(it.title!!)
                            }
                        }
                        Toast.makeText(requireContext(), "from flow", Toast.LENGTH_SHORT).show()
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
            viewModel.sharedFlow.collectLatest { response ->
                when (response) {
                    is Resources.Success -> {
                        response.data?.let { newsResponse ->
                            newsResponse.articles!!.forEach {
                                dataList.add(it.title!!)
                            }
                        }
                        Toast.makeText(requireContext(), "from sharedFlow", Toast.LENGTH_SHORT)
                            .show()
                        setupRecyclerView()
                    }
                    is Resources.Error -> {}
                    is Resources.Loading -> {}
                }
            }
        }
    }

    private fun consumer() {
        //created a coroutine that has a job
        val job = CoroutineScope(Dispatchers.IO).launch {
            //getting first item
            val firstItem = producer().first()
            //getting data in list form
            val listOfUser = producer().toList()
            try {
                producer()
                    .map {
                        it * 2
                        //returns flow
                    }.filter {
                        it == 1
                        //returns flow
                    }//flowOn is doing work in upstreaming , it will change context for the above code only
                    //and the rest of the code can be done in the context defined earlier
                    .flowOn(Dispatchers.IO)
                    //under are the terminal operators which do not return flow
                    .onStart {
//                emit(-1)
//                    Log.d("taget", "started")
                    }.onCompletion {
//                    Log.d("taget", "completed")
                    }.onEach {
//                    Log.d("taget", "onEach-$it")
                    }.collect {
                        //see logcat for results eg: we have 8 users but only see 2 (reference to next block)
//                    Log.d("taget", "$it - ${Thread.currentThread().name}")
                    }
            } catch (e: java.lang.Exception) {
                Log.d("taget", e.toString())
            }
            //buffer is to store emitted items
            val time = measureTimeMillis {
                producer().buffer(3)
                    .collect {
                        //to have delay in consuming
                        delay(2000)
                        Log.d("taget", it.toString())
                    }
            }
            Log.d("taget", time.toString())
        }
        //to cancel a flow just have to stop consumer
        CoroutineScope(Dispatchers.IO).launch {
            delay(50000)
            job.cancel()
        }
    }

    private fun producer() = flow<Int> {
        val list = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 8)
        list.forEach {
            //delay in producing
            delay(1000)
            emit(it)
        }
    }//try/catch only on producer, can commit additinal values
        .catch { Log.d("taget", it.toString()) }

    private fun producerSharedFlow() : Flow<Int> {
        //here we define replay inside mutableSharedFlow as to restore some of the values
        //if the collector is delayed
        val sharedFlow = MutableSharedFlow<Int>(2)
        val list = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 8)
        CoroutineScope(Dispatchers.IO).launch {
            list.forEach {
                //delay in producing
                delay(1000)
                sharedFlow.emit(it)
            }
        }
        return sharedFlow
    }

    private fun consumingSharedFlow() {
        //in this case 2nd consumer is collecting late
        //so it cannot collect first 3 values that have been collected by 1st consumer
        CoroutineScope(Dispatchers.IO).launch {
            producerSharedFlow().collect {
                Log.d("taget1",it.toString())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val r = producerSharedFlow()
            delay(2500)
            r.collect {
                Log.d("taget2",it.toString())
            }
        }
    }
}