package com.example.movieapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movieapp.data.utils.ConstValues
import com.example.movieapp.data.utils.Resource
import com.example.movieapp.data.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _userCreated = MutableLiveData<Resource<Users>>()
    val userCreated: LiveData<Resource<Users>>
        get() = _userCreated

    fun signUp(username: String, email: String, password: String, phone: String) {
        _userCreated.postValue(Resource.Loading)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                userAccount(username, email, password, phone)
            }
            .addOnFailureListener { exception ->
                _userCreated.postValue(Resource.Error(exception))
            }
    }

    private fun userAccount(username: String, email: String, password: String, phone: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val userMap = hashMapOf(
            ConstValues.USER_ID to userId,
            ConstValues.USERNAME to username,
            ConstValues.EMAIL to email,
            ConstValues.PASSWORD to password,
            ConstValues.PHONE to phone
        )
        val refdb = firebaseFirestore.collection(ConstValues.USERS).document(userId)
        refdb.set(userMap)
            .addOnSuccessListener {
                _userCreated.value =
                    Resource.Success(Users(userId, username, email, password, "", "", phone))
            }
            .addOnFailureListener { exception ->
                _userCreated.postValue(Resource.Error(exception))
            }
    }
}