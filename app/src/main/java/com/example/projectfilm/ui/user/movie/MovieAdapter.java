package com.example.projectfilm.ui.user.movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectfilm.R;
import com.example.projectfilm.data.model.Movie;


import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> movieList;
    private OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    public MovieAdapter(Context context, List<Movie> movieList, OnMovieClickListener listener) {
        this.context = context;
        this.movieList = movieList;
        this.listener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_card, parent, false);
        return new MovieViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.titleText.setText(movie.getTitle());
        holder.genreText.setText(movie.getGenre());

        Glide.with(context)
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.placeholder)
                .into(holder.posterImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        TextView titleText, genreText; // genreText bạn có thể bỏ nếu không dùng

        public MovieViewHolder(View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.moviePoster);    // ✅ đúng ID
            titleText = itemView.findViewById(R.id.movieTitle);       // ✅ đúng ID
            genreText = itemView.findViewById(R.id.textStatus);       // ✅ nếu muốn dùng dòng "Đang chiếu"
        }
    }

}
