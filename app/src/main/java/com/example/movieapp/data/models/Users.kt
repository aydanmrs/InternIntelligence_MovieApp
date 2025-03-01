package com.example.movieapp.data.models

data class Users(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val imageUrl: String = "",
    val token:String? = null,
    val phone:String = ""
)
