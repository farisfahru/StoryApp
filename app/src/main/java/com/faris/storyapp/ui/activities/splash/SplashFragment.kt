package com.faris.storyapp.ui.activities.splash

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.faris.storyapp.R
import com.faris.storyapp.databinding.FragmentSplashBinding
import com.faris.storyapp.ui.ViewModelFactory
import com.faris.storyapp.ui.activities.login.LoginViewModel

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory.getInstance(requireActivity())
        val loginViewModel: LoginViewModel by viewModels { factory }
        Handler(Looper.getMainLooper()).postDelayed({
            loginViewModel.getUserToken().observe(viewLifecycleOwner) { token ->
                if (token != null) {
                    view.findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                } else {
                    view.findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                }
            }
        }, 4000)
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivSplash, View.TRANSLATION_Y, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}