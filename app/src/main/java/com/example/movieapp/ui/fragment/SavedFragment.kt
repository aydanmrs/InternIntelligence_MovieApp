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
import com.example.movieapp.data.models.Movie
import com.example.movieapp.data.utils.Resource
import com.example.movieapp.ui.adapters.SavedAdapter
import com.example.movieapp.viewmodel.SavedViewModel
import com.example.movieapp.databinding.FragmentSavedBinding

class SavedFragment : Fragment() {

    private lateinit var binding: FragmentSavedBinding
    private lateinit var savedAdapter: SavedAdapter
    private val viewModel: SavedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapteri təyin et
        savedAdapter = SavedAdapter { movie ->
            movieDetail(movie) // Bütün Movie obyektini ötür
        }
        binding.rvSaves.adapter = savedAdapter

        // GridLayoutManager-i təyin et
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSaves.layoutManager = layoutManager

        // API açarını təyin et
        val apiKey = "827c2738d945feb56a52ad0fc38dc665"

        // Məlumatları yüklə
        viewModel.fetchSavedMovies(apiKey)

        // Məlumatları müşahidə et
        viewModel.movieResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Loading indicator göstər
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvSaves.visibility = View.GONE
                    binding.textEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    // Loading indicator gizlət
                    binding.progressBar.visibility = View.GONE
                    binding.rvSaves.visibility = View.VISIBLE

                    resource.data?.let { movies ->
                        if (movies.isNotEmpty()) {
                            savedAdapter.submitList(movies)
                            binding.textEmpty.visibility = View.GONE
                        } else {
                            // Boş siyahı halında mesaj göstər
                            binding.textEmpty.visibility = View.VISIBLE
                            binding.textEmpty.text = "No saved movies found."
                        }
                    }
                }
                is Resource.Error -> {
                    // Loading indicator gizlət
                    binding.progressBar.visibility = View.GONE
                    binding.rvSaves.visibility = View.GONE
                    binding.textEmpty.visibility = View.VISIBLE
                    binding.textEmpty.text = "Error: ${resource.exception?.message}"

                    Log.e("SavedFragment", "Error: ${resource.exception?.message}")
                }
            }
        }
    }

    private fun movieDetail(movie: Movie) {
        val action = SavedFragmentDirections.actionSavedFragment3ToMovieDetailFragment(movie.title)
        findNavController().navigate(action)
    }
}