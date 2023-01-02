package com.example.settings_playground.repositories

import com.example.settings_playground.api.NewsApi
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class NewsRepo @Inject constructor(private val api: NewsApi) {
    suspend fun topHeadlines(countryCode: String, page: Int) = api.topHeadlines(countryCode, page)
}