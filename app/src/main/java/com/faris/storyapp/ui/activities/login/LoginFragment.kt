package com.faris.storyapp.ui.activities.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.faris.storyapp.R
import com.faris.storyapp.data.Result
import com.faris.storyapp.databinding.FragmentLoginBinding
import com.faris.storyapp.ui.ViewModelFactory
import com.faris.storyapp.utils.Utils

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textListener()
        setupView()
        playAnimation()

        binding.tvAnswer.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)

        }

        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finishAffinity(requireActivity())
                }
            })
    }

    private fun setupView() {
        val email = binding.edtEmail.text
        val password = binding.edtPassword.text
        val data = binding.edtEmail.text
        binding.btnLogin.setOnClickListener {
            if (email.toString().isNotEmpty() && password.toString().isNotEmpty()) {
                observerViewModel(email.toString(), password.toString())
            }
            println("Email: $data")
        }
    }

    private fun textListener() {
        with(binding) {
            edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(p0: Editable?) {}

            })
            edtPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
        }
    }

    private fun setButtonEnable() {
        with(binding) {
            val email = edtEmail.text
            val password = edtPassword.text
            if (email != null && password != null && edtPassword.text.toString().length >= 8 && Utils.isEmailValid(
                    edtEmail.text.toString())
            ) {
                btnLogin.isEnabled = true
            }
        }
    }

    private fun observerViewModel(email: String, password: String) {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val loginViewModel: LoginViewModel by viewModels { factory }
        loginViewModel.getUserToken().observe(viewLifecycleOwner) { token ->
            if (token != null) {
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeFragment)
            }
            loginViewModel.login(email, password).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Success -> {
                            Toast.makeText(requireContext(), "Login berhasil.", Toast.LENGTH_LONG)
                                .show()
                            view?.findNavController()
                                ?.navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(),
                                "Login gagal. Silahkan coba lagi.",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val edtEmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val edtPass = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val tvAnswer = ObjectAnimator.ofFloat(binding.tvAnswer, View.ALPHA, 1f).setDuration(500)
        val tvQuestion = ObjectAnimator.ofFloat(binding.tvQuestion, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(tvQuestion, tvAnswer)
        }

        AnimatorSet().apply {
            playSequentially(edtEmail, edtPass, btnLogin, together)
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}