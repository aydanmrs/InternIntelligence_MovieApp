package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.ui.adapters.GenresMoviesAdapter
import com.example.movieapp.viewmodel.SearchViewModel
import com.example.movieapp.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var genresMoviesAdapter: GenresMoviesAdapter
    private lateinit var viewModel: SearchViewModel // ViewModel-i manual olaraq yaradırıq

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel-i manual olaraq yaradırıq
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        val apiKey = "827c2738d945feb56a52ad0fc38dc665"

        // Adapter-i təyin edirik
        genresMoviesAdapter = GenresMoviesAdapter { movie ->
            movieDetail(movie.title)
        }
        binding.rvGenresMovies.adapter = genresMoviesAdapter
        binding.rvGenresMovies.layoutManager = GridLayoutManager(context, 2)

        // Film məlumatlarını əldə edirik
        viewModel.getMovies(apiKey)

        // Film nəticələrini müşahidə edirik
        viewModel.movieResult.observe(viewLifecycleOwner) { movieResponse ->
            movieResponse?.results?.let { movies ->
                genresMoviesAdapter.submitList(movies)
            }
        }

        // SearchView üçün dinləyici təyin edirik
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchMovies(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    // Axtarış nəticələrini əldə edirik
    private fun searchMovies(query: String) {
        val apiKey = "827c2738d945feb56a52ad0fc38dc665"
        viewModel.getSearch(apiKey, query)

        // Axtarış nəticələrini müşahidə edirik
        viewModel.searchResult.observe(viewLifecycleOwner) { searchResponse ->
            searchResponse?.results?.let { movies ->
                genresMoviesAdapter.submitList(movies)
            }
        }
    }

    // Film detallarına keçid edirik
    private fun movieDetail(movieTitle: String?) {
        if (movieTitle != null) {
            val action = SearchFragmentDirections.actionSearchFragment2ToMovieDetailFragment(movieTitle)
            findNavController().navigate(action)
        }
    }
}
