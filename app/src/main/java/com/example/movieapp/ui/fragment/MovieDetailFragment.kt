package com.example.movieapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.movieapp.data.models.Movie
import com.example.movieapp.viewmodel.MovieDetailViewModel
import com.example.movieapp.ui.adapters.ReviewsAdapter
import com.example.movieapp.databinding.FragmentMovieDetailBinding
import com.google.firebase.auth.FirebaseAuth

class MovieDetailFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailBinding
    private val args: MovieDetailFragmentArgs by navArgs()
    private lateinit var viewModel: MovieDetailViewModel
    private lateinit var auth: FirebaseAuth
    private var videoUrl: String? = null
    private lateinit var reviewAdapter: ReviewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FirebaseAuth obyektini yaradırıq
        auth = FirebaseAuth.getInstance()

        // ViewModel-i yaradırıq
        viewModel = ViewModelProvider(this).get(MovieDetailViewModel::class.java)

        val apiKey = "827c2738d945feb56a52ad0fc38dc665"

        // Filmin məlumatlarını əldə edirik
        viewModel.searchMovieByTitle(apiKey, args.movieTitle)

        // Rəylər üçün adapteri təyin edirik
        reviewAdapter = ReviewsAdapter()
        binding.rvReviews.adapter = reviewAdapter
        binding.rvReviews.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // Müşahidəçiləri təyin edirik
        observeMovieDetails()
        observeTrailerUrl()
        observeReviews()

        // Geri düyməsinin funksionallığı
        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    // Rəyləri müşahidə edirik
    private fun observeReviews() {
        viewModel.reviewDetails.observe(viewLifecycleOwner) { reviews ->
            if (reviews != null && reviews.isNotEmpty()) {
                reviewAdapter.submitList(reviews)
                Log.e("TAGreview", "observeReviews: ${reviews} reviews loaded.")
            } else {
                Toast.makeText(requireContext(), "No reviews available.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Filmin məlumatlarını müşahidə edirik
    private fun observeMovieDetails() {
        viewModel.movieDetails.observe(viewLifecycleOwner) { movie ->
            if (movie != null) {
                populateMovieDetails(movie)
            } else {
                binding.textTitle.text = "No movie found"
            }
        }
    }

    // Treyler URL-ni müşahidə edirik
    private fun observeTrailerUrl() {
        viewModel.trailerUrl.observe(viewLifecycleOwner) { trailerUrl ->
            if (trailerUrl != null) {
                binding.buttonPlay.setOnClickListener {
                    videoUrl = trailerUrl
                    playTrailer(trailerUrl)
                }
            } else {
                Toast.makeText(requireContext(), "No trailer found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Filmin məlumatlarını ekranda göstəririk
    private fun populateMovieDetails(movie: Movie) {
        // Saxlama statusunu yoxlayırıq
        viewModel.checkSaveStatus(movie.id.toString(), binding.iconSave)

        // Filmin məlumatlarını ekranda göstəririk
        binding.textTitle.text = movie.title
        binding.textDescription.text = movie.overview
        binding.textDescription2.text = movie.overview
        binding.textYear.text = movie.release_date

        // Saxlama düyməsinin funksionallığı
        binding.iconSave.setOnClickListener {
            viewModel.toggleSaveStatus(
                movieId = movie.id.toString(),
                posterPath = movie.poster_path,
                imageView = binding.iconSave
            )
        }

        // Şəkilləri yükləyirik
        Glide.with(requireContext())
            .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
            .into(binding.imgMoviePoster)
        Glide.with(requireContext())
            .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
            .into(binding.imgTrailerPoster)
    }

    // Treyleri oynatmaq üçün funksiya
    private fun playTrailer(url: String) {
        val webView: WebView = binding.imgMovieTrailer
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        webView.visibility = View.VISIBLE
    }
}