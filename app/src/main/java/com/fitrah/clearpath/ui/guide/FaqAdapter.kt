package com.fitrah.clearpath.ui.guide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitrah.clearpath.data.model.FaqItem
import com.fitrah.clearpath.databinding.ItemFaqBinding

class FaqAdapter : ListAdapter<FaqItem, FaqAdapter.FaqViewHolder>(FaqDiffCallback()) {

    private val expandedItemIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val binding = ItemFaqBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FaqViewHolder(
        private val binding: ItemFaqBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FaqItem) {
            binding.textCategoryBadge.text = item.category
            binding.textQuestion.text = item.question
            binding.textConciseAnswer.text = item.conciseAnswer
            binding.textDetailedAdvice.text = item.detailedAdvice
            binding.textPracticalTip.text = item.practicalTip

            val isExpanded = expandedItemIds.contains(item.id)
            binding.layoutExpandedDetails.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.imageExpand.rotation = if (isExpanded) 270f else 90f

            binding.root.setOnClickListener {
                if (isExpanded) {
                    expandedItemIds.remove(item.id)
                } else {
                    expandedItemIds.add(item.id)
                }
                notifyItemChanged(bindingAdapterPosition)
            }
        }
    }

    class FaqDiffCallback : DiffUtil.ItemCallback<FaqItem>() {
        override fun areItemsTheSame(oldItem: FaqItem, newItem: FaqItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FaqItem, newItem: FaqItem): Boolean {
            return oldItem == newItem
        }
    }
}
