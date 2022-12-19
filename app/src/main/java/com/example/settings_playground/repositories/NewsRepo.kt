package com.example.settings_playground.repositories

import com.example.settings_playground.api.NewsApi
import com.example.settings_playground.model.NewsResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


class NewsRepo @Inject constructor(private val api: NewsApi) {
    suspend fun topHeadlines(countryCode:String,page:Int): Response<NewsResponse> = api.topHeadlines(countryCode,page)
}