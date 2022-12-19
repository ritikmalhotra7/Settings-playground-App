package com.example.settings_playground.model

import java.io.Serializable

data class NewsResponse(
    val articles: MutableList<Article>?,
    val status: String?,
    val totalResults: Int?
): Serializable{
    override fun toString(): String {
        return "NewsResponse(articles=$articles, status=$status, totalResults=$totalResults)"
    }
}
data class Article (
    var id:Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val url: String,
    val urlToImage: String?
)
data class Source(
    val id: Any,
    val name: String
)
