package com.faris.storyapp.ui.activities.home

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.faris.storyapp.R
import com.faris.storyapp.adapter.LoadingStateAdapter
import com.faris.storyapp.adapter.StoryAdapter
import com.faris.storyapp.databinding.FragmentHomeBinding
import com.faris.storyapp.ui.ViewModelFactory
import com.faris.storyapp.ui.activities.main.MainActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagingApi::class)
@RequiresApi(Build.VERSION_CODES.M)
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupView()

        val factory = ViewModelFactory.getInstance(requireActivity())
        val storiesViewModel: StoriesViewModel by viewModels { factory }
        storiesViewModel.getUserToken().observe(viewLifecycleOwner) { token ->
            if (token.isNullOrEmpty()) {
                view.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            } else {
                storiesViewModel.getStories(token).observe(viewLifecycleOwner) { pagingData ->
                    storyAdapter.submitData(lifecycle, pagingData = pagingData)
                }
            }
        }

        (activity as MainActivity).setSupportActionBar(binding.toolbarMain)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.main_toolbar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.maps -> {
                        view.findNavController().navigate(R.id.action_homeFragment_to_mapsFragment)
                        true
                    }
                    R.id.logout -> {
                        lifecycleScope.launch {
                            storiesViewModel.logout()
                        }
                        view.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED
        )

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    ActivityCompat.finishAffinity(requireActivity())
                }
            })
    }

    private fun setupView() {
        with(binding) {
            fabAddStory.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_homeFragment_to_addStoryFragment)
            }
        }
    }

    private fun setupAdapter() {
        storyAdapter = StoryAdapter()
        with(binding) {
            rvListStory.layoutManager = LinearLayoutManager(requireActivity())
            rvListStory.setHasFixedSize(true)
            rvListStory.adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
    }
}