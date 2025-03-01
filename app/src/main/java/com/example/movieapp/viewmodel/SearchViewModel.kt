package com.example.movieapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.models.MovieResponse
import com.example.movieapp.data.repository.Repository
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    // Repository-ni birbaşa ViewModel daxilində yaradırıq
    private val repository = Repository()

    // MutableLiveData və LiveData üçün dəyişənlər
    private val _movieResult = MutableLiveData<MovieResponse?>()
    val movieResult: LiveData<MovieResponse?>
        get() = _movieResult

    private val _searchResult = MutableLiveData<MovieResponse?>()
    val searchResult: LiveData<MovieResponse?>
        get() = _searchResult

    // Filmləri əldə etmək üçün funksiya
    fun getMovies(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovies(apiKey)
                if (response.isSuccessful) {
                    _movieResult.postValue(response.body())
                } else {
                    _movieResult.postValue(null)
                }
            } catch (e: Exception) {
                _movieResult.postValue(null)
            }
        }
    }

    // Axtarış nəticələrini əldə etmək üçün funksiya
    fun getSearch(apiKey: String, query: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSearch(apiKey, query)
                if (response.isSuccessful) {
                    _searchResult.postValue(response.body())
                } else {
                    _searchResult.postValue(null)
                }
            } catch (e: Exception) {
                _searchResult.postValue(null)
            }
        }
    }
}
