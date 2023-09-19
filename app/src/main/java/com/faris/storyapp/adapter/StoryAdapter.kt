package com.faris.storyapp.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.faris.storyapp.R
import com.faris.storyapp.data.network.response.ListStoryItem
import com.faris.storyapp.databinding.ItemStoryBinding
import com.faris.storyapp.ui.activities.home.HomeFragmentDirections
import com.faris.storyapp.utils.Utils.setLocalDateFormat

@RequiresApi(Build.VERSION_CODES.M)
class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(context: Context, storyItem: ListStoryItem) {
            with(binding) {
                Glide.with(itemView)
                    .load(storyItem.photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imgItem)
                tvName.text = storyItem.name
                tvCreatedTime.setLocalDateFormat(storyItem.createdAt)

                itemView.setOnClickListener {
                    val photoUrl = storyItem.photoUrl
                    val name = storyItem.name
                    val date = storyItem.createdAt
                    val desc = storyItem.description
                    val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(photoUrl, name, date, desc)
                    it.findNavController().navigate(action)
                }

                val loading = CircularProgressDrawable(context)
                loading.setColorSchemeColors(
                    context.getColor(R.color.teal_200),
                    context.getColor(R.color.teal_200),
                    context.getColor(R.color.black)
                )
                loading.centerRadius = 30f
                loading.strokeWidth = 5f
                loading.start()
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemStory = getItem(position)
        if (itemStory != null) {
            holder.bind(holder.itemView.context, itemStory)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

}