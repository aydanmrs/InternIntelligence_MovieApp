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
    val movieResult: LiveData<Resource<List<Movie>>> = _movieResult

    fun fetchSavedMovies(apikey: String) {
        _movieResult.value = Resource.Loading
        firestore.collection("Saves").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val savedPostIds = document.data?.keys?.map { it.toInt() } ?: emptyList()
                    if (savedPostIds.isNotEmpty()) {
                        fetchMoviesByIds(apikey, savedPostIds)
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

    private fun fetchMoviesByIds(apikey: String, movieIds: List<Int>) {
        val movies = mutableListOf<Movie>()
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            val results = movieIds.mapNotNull { movieId ->
                try {
                    val response = repository.getMovieById(movieId, apikey)
                    if (response.isSuccessful) {
                        response.body()?.results?.firstOrNull()
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
