package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.movieapp.R
import com.example.movieapp.data.utils.Resource
import com.example.movieapp.viewmodel.SignUpViewModel
import com.example.movieapp.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
        initNavigationListeners()

        // Geri butonunu idarə et
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Geri butonuna basıldıqda loginFragment-ə yönləndir
                findNavController().navigate(R.id.loginFragment)
            }
        })
    }

    private fun setupViews() {
        binding.signUpButton.setOnClickListener {
            val username = binding.nameInput.text.toString()
            val password = binding.passwordInput.text.toString()
            val email = binding.emailInput.text.toString().trim()
            val phone = binding.numberInput.text.toString().trim()

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(
                    email
                ) || TextUtils.isEmpty(phone)
            ) {
                Toast.makeText(
                    requireContext(),
                    "All fields are required", Toast.LENGTH_SHORT
                ).show()
            } else if (password.length < 6) {
                Toast.makeText(
                    requireContext(),
                    "Password must have at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.signUp(username, email, password, phone)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userCreated.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    // Sign up uğurlu olduqda homeFragment-ə yönləndir
                    findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                }

                is Resource.Error -> {
                    handleSignUpFailure(resource.exception)
                }

                else -> {
                    // Digər hallar üçün heç nə etmə
                }
            }
        })
    }

    private fun handleSignUpFailure(exception: Throwable) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                Toast.makeText(
                    requireContext(),
                    "User with this email already exist",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                Toast.makeText(
                    requireContext(),
                    "Sign up failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initNavigationListeners() {
        binding.arrow.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        binding.signIn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }
}