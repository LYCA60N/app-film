package com.example.projectfilm.ui.user.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectfilm.R;
import com.example.projectfilm.data.model.Movie; // <== IMPORT NÀY PHẢI CÓ

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Movie> movieList = new ArrayList<>();
    private OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(Movie movie);
    }

    public BannerAdapter(OnBannerClickListener listener) {
        this.listener = listener;
    }

    public void setBannerData(List<Movie> movies) {
        this.movieList = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner_image, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Glide.with(holder.imageView.getContext())
                .load(movie.getPosterUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_menu_slideshow)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBannerClick(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImageView); // Phải đúng ID
        }
    }
}
