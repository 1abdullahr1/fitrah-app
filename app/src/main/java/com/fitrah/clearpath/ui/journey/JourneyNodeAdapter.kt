package com.fitrah.clearpath.ui.journey

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.fitrah.clearpath.R
import com.fitrah.clearpath.data.model.JourneyNode
import com.fitrah.clearpath.databinding.ItemJourneyNodeBinding

class JourneyNodeAdapter(
    private val onNodeClicked: (JourneyNode) -> Unit
) : ListAdapter<JourneyNode, JourneyNodeAdapter.NodeViewHolder>(NodeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val binding = ItemJourneyNodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NodeViewHolder(
        private val binding: ItemJourneyNodeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(node: JourneyNode) {
            val context = binding.root.context

            binding.textStepTag.text = "Module ${node.moduleId} • Step ${node.stepOrder}"
            binding.textTitle.text = node.title
            binding.textSubtitle.text = node.subtitle

            // Load micro-image via Coil
            binding.imageThumbnail.load(node.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_chip_rounded)
                error(R.drawable.bg_chip_rounded)
            }

            when {
                node.isCompleted -> {
                    binding.root.alpha = 1.0f
                    binding.imageStatus.setImageResource(R.drawable.ic_check_circle_filled)
                    binding.imageStatus.setColorFilter(
                        ContextCompat.getColor(context, R.color.primary)
                    )
                }
                node.isLocked -> {
                    binding.root.alpha = 0.6f
                    binding.imageStatus.setImageResource(R.drawable.ic_lock_closed)
                    binding.imageStatus.setColorFilter(
                        ContextCompat.getColor(context, R.color.outline)
                    )
                }
                else -> {
                    binding.root.alpha = 1.0f
                    binding.imageStatus.setImageResource(R.drawable.ic_arrow_forward_clean)
                    binding.imageStatus.setColorFilter(
                        ContextCompat.getColor(context, R.color.primary)
                    )
                }
            }

            binding.root.setOnClickListener {
                if (node.isLocked) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.action_locked),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onNodeClicked(node)
                }
            }
        }
    }

    class NodeDiffCallback : DiffUtil.ItemCallback<JourneyNode>() {
        override fun areItemsTheSame(oldItem: JourneyNode, newItem: JourneyNode): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: JourneyNode, newItem: JourneyNode): Boolean {
            return oldItem == newItem
        }
    }
}
