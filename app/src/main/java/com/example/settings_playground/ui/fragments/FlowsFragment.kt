package com.example.settings_playground.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssAntennaInfo.Listener
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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import okio.Buffer
import java.text.DateFormat
import java.util.Date
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
    ): View {
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
         * Flow -> It doesn't have support for state as a result it didn't survive configuration changes we use flow whenever we have a process to do it will do its job and...
         * ...after configuration change the job is resetted.
         * Shared Flow -> It is most similar to live data, serves same function as live data, it will not emit if there is a configuration change so one time job but survive configuration changes
         */
        binding.apply {
            fragmentFlowsBtLiveData.setOnClickListener { viewModel.topHeadlinesToLiveData("US", 1) }
            fragmentFlowsBtStateFlow.setOnClickListener {
                viewModel.topHeadLinesToStateFlow(
                    "US",
                    2
                )
            }
            fragmentFlowsBtFlow.setOnClickListener { observeFlow() }
            fragmentFlowsBtSharedFlow.setOnClickListener {
                viewModel.topHeadLinesToSharedFlow(
                    "US",
                    2
                )
            }
            rvAdapter = NewsAdapter().apply {
                setList(dataList)
            }
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
                        Log.d("taget2", "stateFlow")
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
//                    Log.d("taget2", "started")
                    }.onCompletion {
//                    Log.d("taget2", "completed")
                    }.onEach {
//                    Log.d("taget2", "onEach-$it")
                    }.collect {
                        //see logcat for results eg: we have 8 users but only see 2 (reference to next block)
//                    Log.d("taget2", "$it - ${Thread.currentThread().name}")
                    }
            } catch (e: java.lang.Exception) {
                Log.d("taget2", e.toString())
            }
            //buffer is to store emitted items
            val time = measureTimeMillis {
                producer().buffer(3)
                    .collect {
                        //to have delay in consuming
                        delay(2000)
                        Log.d("taget2", it.toString())
                    }
            }
            Log.d("taget2", time.toString())
        }
        //to cancel a flow just have to stop consumer
        CoroutineScope(Dispatchers.IO).launch {
            delay(50000)
            job.cancel()
        }
    }

    private fun producer() = flow {
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8)
        list.forEach {
            //delay in producing
            delay(1000)
            emit(it)
        }
    }//catch only on producer, can commit additional values
        .catch { Log.d("taget2", it.toString()) }

    private fun producerSharedFlow(): SharedFlow<Int> {//or the return value can be SharedFlow()
        //here we define replay inside mutableSharedFlow as to restore some of the values
        //if the collector is delayed
        val sharedFlow = MutableSharedFlow<Int>(/*1*/)
        val list = listOf(1, 2, 3)
        CoroutineScope(Dispatchers.IO).launch {
            list.forEach {
                //delay in producing
//                delay(1000)
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
                Log.d("taget21", it.toString())
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val r = producerSharedFlow()
            r.buffer().collect {
                Log.d("taget22", it.toString())
            }
        }
    }

    private fun producerStateFlow(): StateFlow<Int> {
        // state flow just stores that state of latest value given to it
        //here we define default value inside mutableStateFlow as to have some state by default
        val stateFlow = MutableStateFlow<Int>(10)
        val list = listOf(1, 2)
        CoroutineScope(Dispatchers.IO).launch {
            list.forEach {
                //delay in producing
                delay(1000)
                stateFlow.emit(it)
            }
        }
        return stateFlow
    }

    private fun consumingStateFlow() {
        //in this case consumer is collecting late so have only the latest value
        CoroutineScope(Dispatchers.IO).launch {
            // returns what it have store at last
            val result = producerStateFlow()
            delay(3000)
            result.buffer(2).collect {
                Log.d("taget21", it.toString())
            }
        }
    }
    override fun onAttach(context: Context) {
        Log.d("taget2","onAttach")
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("taget2","onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        Log.d("taget2","onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("taget2","onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("taget2","onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("taget2","onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("taget2","onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        Log.d("taget2","onDetach")
        super.onDetach()
    }

    override fun onDestroyView() {
        Log.d("taget2","onDestroyView")
        super.onDestroyView()
    }

}