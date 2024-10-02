package com.example.settings_playground.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.settings_playground.databinding.LayoutItemConfigurationBinding
import com.example.settings_playground.model.ItemConfiguration

class ConfigurationAdapter : RecyclerView.Adapter<ConfigurationAdapter.ViewHolder>() {
    private val currentList = arrayListOf<ItemConfiguration>()
    fun setList(list: ArrayList<ItemConfiguration>) {
        currentList.clear()
        currentList.addAll(list)
        notifyItemRangeChanged(0, list.size)
    }

    private var textChangedListener: ((ItemConfiguration) -> Unit)? = null
    fun setTextChangedListener(listener: (ItemConfiguration) -> Unit) {
        textChangedListener = listener
    }

    inner class ViewHolder(private val binding: LayoutItemConfigurationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var textWatcher: TextWatcher? = null
        fun setData(item: ItemConfiguration) {
            binding.apply {
                item.apply {
                    tv1.text = item.string1
                    textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                        }
                        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun afterTextChanged(p0: Editable?) {
                            textChangedListener?.invoke(item.copy(string2 = p0.toString()))
                        }
                    }
                    tiet1.removeTextChangedListener(textWatcher)
                    tiet1.setText(item.string2)
                    tiet1.addTextChangedListener(textWatcher)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutItemConfigurationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(currentList[position])
    }
}