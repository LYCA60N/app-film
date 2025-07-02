package com.example.projectfilm.data.viewmodels.user.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;

import com.example.projectfilm.data.model.Movie;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Movie>> moviesLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> bannerUrls = new MutableLiveData<>(); // ✅ THÊM

    public LiveData<List<Movie>> getMovies() {
        if (moviesLiveData.getValue() == null) {
            loadMoviesFromFirestore();
        }
        return moviesLiveData;
    }

    public LiveData<List<String>> getBannerUrls() { // ✅ THÊM
        return bannerUrls;
    }

    private void loadMoviesFromFirestore() {
        FirebaseFirestore.getInstance().collection("movies")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Movie> movies = new ArrayList<>();
                    List<String> banners = new ArrayList<>(); // ✅ THÊM

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Movie movie = doc.toObject(Movie.class);
                        if (movie != null) {
                            movies.add(movie);
                            // ✅ THÊM: Lấy ảnh banner từ trường imageUrl
                            if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
                                banners.add(movie.getPosterUrl());
                            }
                        }
                    }

                    moviesLiveData.setValue(movies);
                    bannerUrls.setValue(banners); // ✅ Cập nhật ảnh banner
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeVM", "Firestore error: ", e);
                });
    }
}
