package com.example.movieapp.viewmodel

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.models.Movie
import com.example.movieapp.R
import com.example.movieapp.data.repository.Repository
import com.example.movieapp.data.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch

class MovieDetailViewModel : ViewModel() {
    private val repository = Repository() // Repository obyektini manual olaraq yaradırıq
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance() // FirebaseAuth obyektini manual olaraq yaradırıq
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance() // FirebaseFirestore obyektini manual olaraq yaradırıq

    private val _movieDetails = MutableLiveData<Movie?>()
    val movieDetails: LiveData<Movie?>
        get() = _movieDetails

    private val _trailerUrl = MutableLiveData<String?>()
    val trailerUrl: LiveData<String?>
        get() = _trailerUrl

    private val _reviewDetails = MutableLiveData<List<Review>?>()
    val reviewDetails: LiveData<List<Review>?>
        get() = _reviewDetails

    // Filmi saxlamaq üçün funksiya
    fun addSaveToFirebase(postId: String) {
        val savedData = hashMapOf(
            postId to true
        )
        firebaseFirestore.collection("Saves").document(firebaseAuth.currentUser!!.uid)
            .set(savedData, SetOptions.merge())
            .addOnSuccessListener {
                // Success handling can be added here if needed
            }
            .addOnFailureListener { exception ->
                // Failure handling can be added here if needed
            }
    }

    // Saxlanılan filmi silmək üçün funksiya
    fun removeSaveFromFirestore(postId: String) {
        firebaseFirestore.collection("Saves").document(firebaseAuth.currentUser!!.uid)
            .update(postId, FieldValue.delete())
            .addOnSuccessListener {
                // Success handling can be added here if needed
            }
            .addOnFailureListener { exception ->
                // Failure handling can be added here if needed
            }
    }

    // Saxlama statusunu yoxlamaq üçün funksiya
    fun checkSaveStatus(postId: String, imageView: ImageView) {
        firebaseFirestore.collection("Saves").document(firebaseAuth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val savedPostId = document.getBoolean(postId) ?: false
                    if (savedPostId) {
                        imageView.setImageResource(R.drawable.ic_saved)
                        imageView.tag = "saved"
                    } else {
                        imageView.setImageResource(R.drawable.ic_save)
                        imageView.tag = "save"
                    }
                } else {
                    imageView.setImageResource(R.drawable.ic_save)
                    imageView.tag = "save"
                }
            }
            .addOnFailureListener { exception ->
                // Failure handling can be added here if needed
            }
    }

    // Saxlama statusunu dəyişdirmək üçün funksiya
    fun toggleSaveStatus(postId: String, imageView: ImageView) {
        val tag = imageView.tag?.toString() ?: ""

        if (tag == "saved") {
            imageView.setImageResource(R.drawable.ic_save)
            imageView.tag = "save"
            removeSaveFromFirestore(postId)
        } else {
            imageView.setImageResource(R.drawable.ic_saved)
            imageView.tag = "saved"
            addSaveToFirebase(postId)
        }
    }

    // Filmi başlığa görə axtarmaq üçün funksiya
    fun searchMovieByTitle(apiKey: String, title: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSearch(apiKey, title)
                if (response.isSuccessful) {
                    val movies = response.body()?.results
                    if (!movies.isNullOrEmpty()) {
                        _movieDetails.postValue(movies.first())
                        val movieId = movies.first().id
                        getMovieTrailer(apiKey, movieId)
                        getReviews(apiKey, movieId)
                    }
                }
            } catch (e: Exception) {
                // Error handling can be added here if needed
            }
        }
    }

    // Filmin treylerini əldə etmək üçün funksiya
    private suspend fun getMovieTrailer(apiKey: String, movieId: Int) {
        try {
            val response = repository.getVideos(movieId, apiKey)
            if (response.isSuccessful) {
                val videos = response.body()?.results
                if (!videos.isNullOrEmpty()) {
                    val trailer = videos.first()
                    val videoUrl = "https://www.youtube.com/embed/${trailer.key}"
                    _trailerUrl.postValue(videoUrl)
                }
            }
        } catch (e: Exception) {
            // Error handling can be added here if needed
        }
    }

    // Rəyləri əldə etmək üçün funksiya
    private suspend fun getReviews(apiKey: String, movieId: Int) {
        try {
            val response = repository.getReviews(movieId, apiKey)
            if (response.isSuccessful) {
                val reviews = response.body()?.results
                _reviewDetails.postValue(reviews ?: emptyList())
            }
        } catch (e: Exception) {
            // Error handling can be added here if needed
        }
    }
}
