package com.faris.storyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.faris.storyapp.R
import com.faris.storyapp.databinding.ItemLoadingStoryBinding

class LoadingStateAdapter(private val retry: () -> Unit) :
LoadStateAdapter<LoadingStateAdapter.ViewHolder>(){
    class ViewHolder(private val binding: ItemLoadingStoryBinding, retry: () -> Unit) :
    RecyclerView.ViewHolder(binding.root){
        fun bind(loadState: LoadState) {
            with(binding){
                if (loadState is LoadState.Error) {
                    tvError.text = root.context.getString(R.string.loading_error_message)
                }
                progressBar.isVisible = loadState is LoadState.Loading
                btnRetry.isVisible = loadState is LoadState.Error
                tvError.isVisible = loadState is LoadState.Error
            }

        }
        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val view = ItemLoadingStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view, retry)
    }
}