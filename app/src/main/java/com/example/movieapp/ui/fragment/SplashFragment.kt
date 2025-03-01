package com.example.movieapp.ui.fragment

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.movieapp.R
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        val lottieAnimationView = view.findViewById<LottieAnimationView>(R.id.lottie)
        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // SharedPreferences ilə ilk açılışı yoxla
                val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

                // Firebase Auth ilə istifadəçinin login olub-olmadığını yoxla
                val firebaseAuth = FirebaseAuth.getInstance()
                val currentUser = firebaseAuth.currentUser

                if (isFirstLaunch) {
                    // İlk dəfə açılış: onboardingFragment-ə yönləndir
                    findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
                    // İlk açılış olduğunu qeyd et
                    sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
                } else {
                    // İlk dəfə deyil: istifadəçi login olubsa homeFragment-ə, olmayıbsa loginFragment-ə yönləndir
                    if (currentUser != null) {
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                    } else {
                        findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })

        return view
    }
}