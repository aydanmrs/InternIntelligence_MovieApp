package com.example.movieapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.models.GenreResponse
import com.example.movieapp.data.models.MovieResponse
import com.example.movieapp.data.repository.Repository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = Repository()

    private val _popularMovieResult = MutableLiveData<MovieResponse?>()
    val popularMovieResult: LiveData<MovieResponse?>
        get() = _popularMovieResult
    private val _topRatedMovieResult = MutableLiveData<MovieResponse?>()
    val topRatedMovieResult: LiveData<MovieResponse?>
        get() = _topRatedMovieResult

    private val _nowPlayingMoviesResult = MutableLiveData<MovieResponse?>()
    val nowPlayingMoviesResult: LiveData<MovieResponse?>
        get() = _nowPlayingMoviesResult

    private val _movieGenres = MutableLiveData<GenreResponse?>()
    val movieGenres: LiveData<GenreResponse?>
        get() = _movieGenres

    private val _movieGenresMovies = MutableLiveData<MovieResponse?>()
    val movieGenresMovies: LiveData<MovieResponse?>
        get() = _movieGenresMovies

    fun getPopularMovies(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.getPopularMovies(apiKey)
                if (response.isSuccessful) {
                    _popularMovieResult.postValue(response.body())
                } else {
                    _popularMovieResult.postValue(null)
                }
            } catch (e: Exception) {
                _popularMovieResult.postValue(null)
            }
        }
    }

    fun getTopRatedMovies(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTopRatedMovies(apiKey)
                if (response.isSuccessful) {
                    _topRatedMovieResult.postValue(response.body())
                } else {
                    _topRatedMovieResult.postValue(null)
                }
            } catch (e: Exception) {
                _topRatedMovieResult.postValue(null)
            }
        }
    }

    fun getNowPlayingMovies(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.getNowPlayingMovies(apiKey)
                if (response.isSuccessful) {
                    _nowPlayingMoviesResult.postValue(response.body())
                } else {
                    _nowPlayingMoviesResult.postValue(null)
                }
            } catch (e: Exception) {
                _nowPlayingMoviesResult.postValue(null)
            }
        }
    }

    fun getGenres(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.getGenres(apiKey)
                if (response.isSuccessful) {
                    _movieGenres.postValue(response.body())
                } else {
                    _movieGenres.postValue(null)
                }
            } catch (e: Exception) {
                _movieGenres.postValue(null)
            }
        }
    }

    fun getGenreMovies(apiKey: String, genreId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getCategoryMovies(apiKey, genreId)
                if (response.isSuccessful) {
                    _movieGenresMovies.postValue(response.body())
                } else {
                    _movieGenresMovies.postValue(null)
                }
            } catch (e: Exception) {
                _movieGenresMovies.postValue(null)
            }
        }
    }
}
