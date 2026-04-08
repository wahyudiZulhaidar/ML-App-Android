package ru.rst.penyakitbawang.presentation

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.rst.penyakitbawang.database.DataHistoryEntity
import ru.rst.penyakitbawang.databinding.ItemHistoryBinding


class HistoryAdapter(private val onItemClicked: (DataHistoryEntity) -> Unit) :
    ListAdapter<DataHistoryEntity, HistoryAdapter.HistoryViewHolder>(DataHistoryDiffCallback()) {
    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: DataHistoryEntity) {
            binding.imgEvent.setImageURI(item.imagePath.toUri())
            Log.d("HistoryAdapter", "bind: ${item.imagePath}")
            binding.tvCategoryEvent.text = "${item.result} ${item.score}"
            binding.cardView2.setOnClickListener {
                onItemClicked(
                    item
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class DataHistoryDiffCallback : DiffUtil.ItemCallback<DataHistoryEntity>() {
    override fun areItemsTheSame(oldItem: DataHistoryEntity, newItem: DataHistoryEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: DataHistoryEntity,
        newItem: DataHistoryEntity
    ): Boolean {
        return oldItem == newItem
    }
}
