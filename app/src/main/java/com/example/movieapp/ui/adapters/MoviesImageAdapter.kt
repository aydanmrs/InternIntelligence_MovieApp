package com.example.movieapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.ItemImageBinding

class MoviesImageAdapter(
    private val imageUrls: List<String>,
    private val onImageClick: () -> Unit
) :
    RecyclerView.Adapter<MoviesImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val baseUrl = "https://image.tmdb.org/t/p/w500"
        Glide.with(holder.binding.root.context)
            .load("$baseUrl${imageUrls[position]}")
            .into(holder.binding.imageView)
        holder.binding.imageView.setOnClickListener {
            onImageClick()
        }
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }
}