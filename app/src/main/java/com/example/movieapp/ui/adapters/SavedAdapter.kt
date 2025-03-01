package com.example.movieapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.data.models.Movie
import com.example.movieapp.databinding.ItemTopMoviesBinding

class SavedAdapter(var itemClick: (item: Movie) -> Unit): RecyclerView.Adapter<SavedAdapter.SavedViewHolder>() {

    // DiffUtil Callback
    private val diffUtilCallBack = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            // Filmlərin unikal ID-lərinə əsaslanın
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            // Filmlərin bütün məlumatlarını müqayisə edin
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffUtilCallBack)

    // Adapterə yeni məlumatları göndərmək üçün
    fun submitList(movieList: List<Movie>) {
        differ.submitList(movieList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val binding = ItemTopMoviesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    inner class SavedViewHolder(private val binding: ItemTopMoviesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            val baseUrl = "https://image.tmdb.org/t/p/w500"  // TMDB şəkil URL formatı
            Glide.with(binding.imgMoviePoster.context)
                .load("$baseUrl${movie.poster_path}")
                .into(binding.imgMoviePoster)

            // Filmə klikləmə hadisəsi
            itemView.setOnClickListener {
                itemClick(movie)
            }
        }
    }
}
