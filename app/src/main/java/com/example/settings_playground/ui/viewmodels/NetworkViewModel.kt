package com.example.settings_playground.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.settings_playground.utils.Resources
import com.example.settings_playground.model.NewsResponse
import com.example.settings_playground.repositories.NewsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(private val newsRepo: NewsRepo) : ViewModel() {
    private val _liveData: MutableLiveData<Resources<NewsResponse>> = MutableLiveData()
    val liveData get() = _liveData


    private val _stateFlow:MutableStateFlow<Resources<NewsResponse>> = MutableStateFlow(Resources.Loading())
    val stateFlow get() = _stateFlow.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<Resources<NewsResponse>>()
    val sharedFlow get() = _sharedFlow.asSharedFlow()

    fun topHeadlinesToLiveData(countryCode: String, page: Int) = viewModelScope.launch {
        _liveData.postValue(Resources.Loading())
        val result = newsRepo.topHeadlines(countryCode = countryCode, page = page)
        _liveData.value = handleData(result = result)
    }

    private fun handleData(result: Response<NewsResponse>): Resources<NewsResponse> {
        if (result.isSuccessful) {
            result.body()?.let {
                return Resources.Success(it)
            } ?: return Resources.Error(result.errorBody().toString())
        } else {
            return Resources.Error(result.errorBody().toString())
        }
    }

    fun topHeadLinesToStateFlow(countryCode: String, page: Int) = viewModelScope.launch {
        val result = newsRepo.topHeadlines(countryCode = countryCode, page = page)
        _stateFlow.value = handleData(result)
    }
    fun topHeadLinesToFlow(countryCode: String, page: Int): Flow<Resources<NewsResponse>> {
        return flow{
            emit(handleData(newsRepo.topHeadlines(countryCode = countryCode,page = page)))
        }
    }
    fun topHeadLinesToSharedFlow(countryCode: String, page: Int) = viewModelScope.launch {
        _sharedFlow.emit(Resources.Loading())
        _sharedFlow.emit(handleData(newsRepo.topHeadlines(countryCode = countryCode, page = page)))
    }
}
