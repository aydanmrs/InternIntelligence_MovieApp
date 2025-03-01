package com.example.movieapp.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.movieapp.viewmodel.ProfileViewModel
import com.example.movieapp.R
import com.example.movieapp.data.utils.Resource
import com.example.movieapp.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        buttonLogout()

        viewModel.fetchUserInformation()

        viewModel.userInformation.observe(viewLifecycleOwner) { userResource ->
            when (userResource) {
                is Resource.Success -> {
                    val user = userResource.data
                    if (user != null) {
                        binding.textName.text = "Hi, ${user.username}!"
                    } else {
                        binding.textName.text = "Hi, Guest!"
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), "${userResource.exception}", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    // Yükləmə zamanı nə ediləcək (məsələn, ProgressBar göstərmək)
                }
            }
        }
    }

    private fun buttonLogout() {
        binding.logOutButton.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.logout_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val btnLogout: TextView = dialog.findViewById(R.id.btnLogOut)
            val btnCancel: TextView = dialog.findViewById(R.id.btnCancel)

            btnLogout.setOnClickListener {
                viewModel.auth.signOut() // İndi çıxış var
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                dialog.dismiss()
            }
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}