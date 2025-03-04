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

    // Filmi Firebase-ə əlavə etmək üçün funksiya
    fun addSaveToFirebase(movieId: String, posterPath: String?) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val saveData = hashMapOf(
            "posterPath" to posterPath,
            "isSaved" to true
        )

        firebaseFirestore.collection("Saves")
            .document(userId)
            .set(mapOf(movieId to saveData), SetOptions.merge())
            .addOnSuccessListener {
                // Uğurlu əməliyyat
            }
            .addOnFailureListener { exception ->
                // Xəta baş verdi
            }
    }

    // Filmi Firebase-dən silmək üçün funksiya
    fun removeSaveFromFirestore(movieId: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firebaseFirestore.collection("Saves")
            .document(userId)
            .update(movieId, FieldValue.delete())
            .addOnSuccessListener {
                // Uğurlu əməliyyat
            }
            .addOnFailureListener { exception ->
                // Xəta baş verdi
            }
    }

    // Saxlama statusunu yoxlamaq üçün funksiya
    fun checkSaveStatus(movieId: String, imageView: ImageView) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        firebaseFirestore.collection("Saves")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val movieData = document.get(movieId) as? Map<*, *>
                    val isSaved = movieData?.get("isSaved") as? Boolean ?: false

                    if (isSaved) {
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
                // Xəta baş verdi
            }
    }

    // Saxlama statusunu dəyişdirmək üçün funksiya
    fun toggleSaveStatus(movieId: String, posterPath: String?, imageView: ImageView) {
        val tag = imageView.tag?.toString() ?: ""

        if (tag == "saved") {
            imageView.setImageResource(R.drawable.ic_save)
            imageView.tag = "save"
            removeSaveFromFirestore(movieId)
        } else {
            imageView.setImageResource(R.drawable.ic_saved)
            imageView.tag = "saved"
            addSaveToFirebase(movieId, posterPath)
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
