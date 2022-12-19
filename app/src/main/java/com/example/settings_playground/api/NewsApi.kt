package com.example.settings_playground.api

import com.example.settings_playground.model.NewsResponse
import com.example.settings_playground.utils.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("top-headlines")
    suspend fun topHeadlines(
        @Query("country") countryCode: String,
        @Query("page") page: Int,
        @Query("apiKey") key: String = API_KEY
    ): Response<NewsResponse>

}