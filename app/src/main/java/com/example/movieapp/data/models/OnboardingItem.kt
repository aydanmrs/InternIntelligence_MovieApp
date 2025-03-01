package com.example.movieapp.data.models

import androidx.annotation.DrawableRes

data class OnboardingItem(
    val title: String,
    val description: String,
    @DrawableRes val imageResId: Int
)