package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.data.utils.Resource
import com.example.movieapp.ui.adapters.SavedAdapter
import com.example.movieapp.viewmodel.SavedViewModel
import com.example.movieapp.databinding.FragmentSavedBinding


class SavedFragment : Fragment() {
    private lateinit var binding: FragmentSavedBinding
    private lateinit var savedAdapter: SavedAdapter
    val viewModel: SavedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedAdapter = SavedAdapter { movie ->
            movieDetail(movie.title)
        }
        binding.rvSaves.adapter = savedAdapter
        binding.rvSaves.layoutManager = GridLayoutManager(context, 2)

        val apiKey = "827c2738d945feb56a52ad0fc38dc665"
        viewModel.fetchSavedMovies(apiKey)

        viewModel.movieResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    resource.data?.let { movies ->
                        if (movies.isNotEmpty()) {
                            savedAdapter.submitList(movies)
                            Log.e("TAG", "Adapter list: $movies")
                        } else {
                        }
                    }
                }
                is Resource.Error -> {
                    Log.e("SavedFragment", "Error: ${resource.exception?.message}")
                }
            }
        }

    }

    private fun movieDetail(movieTitle: String?) {
        if (movieTitle != null) {
            val action = SavedFragmentDirections.actionSavedFragment3ToMovieDetailFragment(movieTitle)
            findNavController().navigate(action)
        } else {
            Log.e("SavedFragment", "Movie title is null")
        }
    }
}
