package com.faris.storyapp.ui.activities.register

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
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.faris.storyapp.R
import com.faris.storyapp.data.Result
import com.faris.storyapp.databinding.FragmentRegisterBinding
import com.faris.storyapp.ui.ViewModelFactory
import com.faris.storyapp.utils.Utils

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textListener()
        setupView()
        playAnimation()
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
            val name = edtName.text
            val email = edtEmail.text
            val password = edtPassword.text
            if (name != null && email != null && password != null && edtPassword.text.toString().length >= 8 && Utils.isEmailValid(
                    edtEmail.text.toString())
            ) {
                btnRegister.isEnabled = true
            }
        }
    }

    private fun setupView() {
        with(binding) {
            btnRegister.setOnClickListener {
                val name = binding.edtName.text.toString()
                val email = binding.edtEmail.text.toString()
                val password = binding.edtPassword.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    observerViewModel(name, email, password)
                }
                println("Nama : $name")
            }
            tvAnswer.setOnClickListener {
                view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
    }

    private fun observerViewModel(name: String, email: String, password: String) {
        val factory = ViewModelFactory.getInstance(requireActivity())
        val registerViewModel: RegisterViewModel by viewModels { factory }

        registerViewModel.register(name, email, password).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        Toast.makeText(requireContext(), "Akun berhasil dibuat.", Toast.LENGTH_LONG)
                            .show()
                        view?.findNavController()
                            ?.navigate(R.id.action_registerFragment_to_loginFragment)

                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),
                            "Registrasi gagal. Silahkan coba lagi.",
                            Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val edtName = ObjectAnimator.ofFloat(binding.edtName, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val edtPass = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val btnRegister =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)
        val tvAnswer = ObjectAnimator.ofFloat(binding.tvAnswer, View.ALPHA, 1f).setDuration(500)
        val tvQuestion = ObjectAnimator.ofFloat(binding.tvQuestion, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(tvQuestion, tvAnswer)
        }

        AnimatorSet().apply {
            playSequentially(edtName, edtEmail, edtPass, btnRegister, together)
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}