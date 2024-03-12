package com.albara.bestapptask.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.albara.bestapptask.R
import com.albara.bestapptask.data.objects.Movie
import com.albara.bestapptask.databinding.RvItemBinding
import com.albara.bestapptask.util.DataDiffCallback
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class RvAdapter (private val rvActionsListener : RvActionsListener)
    : RecyclerView.Adapter<RvAdapter.ViewHolder>() {

    private var movies = emptyList<Movie>()

    inner class ViewHolder(private val binding : RvItemBinding) :
        RecyclerView.ViewHolder(binding.root), Callback {
        fun bind(movie: Movie) {
            binding.cbFavorite.isChecked = movie.isFavorited
            binding.cbFavorite.setOnCheckedChangeListener { _, isChecked ->
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    val selectedMovie = movies[position]
                    selectedMovie.isFavorited = isChecked
                    rvActionsListener.onFavoriteCheckedChange(selectedMovie)
                }
            }
            binding.root.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION) {
                    val selectedMovie = movies[position]
                    rvActionsListener.onItemClick(selectedMovie)
                }
            }
            Picasso.get().load(movie.poster.previewUrl)
                .error(R.drawable.ic_default_icon)
                .into(binding.ivPreview, this)
            binding.tvTitle.text = movie.name
            val rating  = "IMDb : ${movie.rating.imdb}"
            binding.tvRating.text = rating
        }

        override fun onSuccess() {
            binding.ivPreview.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        override fun onError(e: Exception?) {
            binding.ivPreview.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    fun setMoviesList(movies : List<Movie>){
        val diffResult = DiffUtil.calculateDiff(DataDiffCallback(this.movies, movies))
        this.movies = movies
        diffResult.dispatchUpdatesTo(this)
    }

    
}