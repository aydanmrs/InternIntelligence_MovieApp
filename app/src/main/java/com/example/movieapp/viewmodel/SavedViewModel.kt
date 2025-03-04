package com.example.movieapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movieapp.data.models.Movie
import com.example.movieapp.data.repository.Repository
import com.example.movieapp.data.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SavedViewModel : ViewModel() {

    private val repository = Repository()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _movieResult = MutableLiveData<Resource<List<Movie>>>()
    val movieResult: LiveData<Resource<List<Movie>>> get() = _movieResult

    fun fetchSavedMovies(apiKey: String) {
        _movieResult.value = Resource.Loading
        firestore.collection("Saves").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Firestore-dan bütün saxlanılan filmlərin məlumatlarını əldə edirik
                    val savedMovies = document.data?.mapNotNull { (movieId, movieData) ->
                        val movieMap = movieData as? Map<*, *>
                        val posterPath = movieMap?.get("posterPath") as? String
                        if (posterPath != null) {
                            Movie(
                                id = movieId.toInt(),
                                title = "", // Bu hissəni sonradan doldururuq
                                overview = "",
                                poster_path = posterPath,
                                release_date = "",
                                isSaved = true
                            )
                        } else {
                            null
                        }
                    } ?: emptyList()

                    if (savedMovies.isNotEmpty()) {
                        // Filmlərin tam məlumatlarını əldə etmək üçün API-yə sorğu göndəririk
                        fetchMoviesByIds(apiKey, savedMovies)
                    } else {
                        _movieResult.value = Resource.Success(emptyList())
                    }
                } else {
                    _movieResult.value = Resource.Success(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                _movieResult.value = Resource.Error(exception)
            }
    }

    private fun fetchMoviesByIds(apiKey: String, savedMovies: List<Movie>) {
        val movies = mutableListOf<Movie>()
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            val results = savedMovies.mapNotNull { movie ->
                try {
                    // API-dən filmin tam məlumatlarını əldə edirik
                    val response = repository.getMovieById(movie.id, apiKey)
                    if (response.isSuccessful) {
                        val fullMovie = response.body()?.results?.firstOrNull()
                        fullMovie?.copy(isSaved = true) // Filmin saxlanıldığını qeyd edirik
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            movies.addAll(results)
            _movieResult.postValue(Resource.Success(movies))
        }
    }
}