package com.example.settings_playground.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.settings_playground.databinding.ItemNewsBinding

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ViewHolder>(){

    private var onCreateCount = 0
    private var onBindCount = 0
    private var onRecyclerViewCount = 0
    private val callback = object: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: String, newItem: String): Any? {
            return super.getChangePayload(oldItem, newItem)
        }
    }
    val differ = AsyncListDiffer(this,callback)

    fun setList(list:ArrayList<String>){
        differ.submitList(list)
    }

    class ViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(data: String, position: Int) {
            binding.apply {
                itemNewsTv.text = data
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        onCreateCount++
        Log.d("taget-onCreate:",onCreateCount.toString())
        return ViewHolder(ItemNewsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.setData(item,position)
        onBindCount++;
        Log.d("taget-onBind:",onBindCount.toString())
    }

    override fun onViewRecycled(holder: ViewHolder) {
        Log.d("taget-recycled-vie-count",(++onRecyclerViewCount).toString())
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int = differ.currentList.size

}