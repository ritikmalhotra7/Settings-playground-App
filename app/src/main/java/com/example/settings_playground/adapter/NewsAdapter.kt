package com.example.settings_playground.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.settings_playground.databinding.ItemNewsBinding

class NewsAdapter(private val dataList: ArrayList<String>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(data: String, position: Int) {
            binding.apply {
                itemNewsTv.text = data
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(dataList[position],position)
    }

    override fun getItemCount(): Int = dataList.size
}