package com.faris.storyapp.ui.activities.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.faris.storyapp.databinding.FragmentDetailBinding
import com.faris.storyapp.utils.Utils.setLocalDateFormat

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        with(binding) {
            Glide.with(requireActivity())
                .load(args.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivStory)
            tvName.text = args.name
            tvCreatedTime.setLocalDateFormat(args.createdAt)
            tvDescription.text = args.description

            toolbarDetail.setOnClickListener {
                view?.findNavController()?.popBackStack()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}