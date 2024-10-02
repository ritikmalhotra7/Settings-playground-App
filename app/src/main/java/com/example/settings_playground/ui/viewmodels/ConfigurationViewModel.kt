package com.example.settings_playground.ui.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.settings_playground.model.ItemConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _list = MutableStateFlow<ArrayList<ItemConfiguration>>(arrayListOf())
    val list: StateFlow<ArrayList<ItemConfiguration>> =
        _list.filterNotNull().onStart {
            Log.d("onStart-taget", "onStart")
            loadData()
        }.onCompletion {
            Log.d("onCompletion-taget", "onCompletion")
        }.flowOn(Dispatchers.IO).stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), arrayListOf()
        )

    private fun loadData() {
        savedStateHandle["items"] = arrayListOf(
            ItemConfiguration("1", "1", "one"),
            ItemConfiguration("2", "2", "two"),
        )
        _list.update {
            savedStateHandle.get<ArrayList<ItemConfiguration>>("items") ?: arrayListOf()
        }
    }

    fun onTextChanged(item: ItemConfiguration) {
        Log.d("item-to-be-changed", item.toString())
        savedStateHandle["items"] =
            (savedStateHandle.get<ArrayList<ItemConfiguration>>("items") ?: arrayListOf()).map {
                if (it.id == item.id) item
                else it
            }
    }

    fun apiCall() {
        Log.d("list.value", list.value.toString())
        Log.d(
            "savedStateHandle.value",
            savedStateHandle.get<ArrayList<ItemConfiguration>>("items").toString()
        )
    }

    fun onConfigurationChange() {
        Log.d("onConfigurationChange-taget", "onConfigurationChange")
        _list.update {
            savedStateHandle.get<ArrayList<ItemConfiguration>>("items") ?: arrayListOf()
        }
    }
}