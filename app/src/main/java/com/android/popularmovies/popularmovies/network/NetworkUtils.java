package com.android.popularmovies.popularmovies.network;

import com.android.popularmovies.popularmovies.BuildConfig;
import com.android.popularmovies.popularmovies.entity.MoviesResponse;
import com.android.popularmovies.popularmovies.entity.ReviewsResponse;
import com.android.popularmovies.popularmovies.entity.VideosResponse;

import retrofit2.Call;

/**
 * Created by lei on 6/14/17.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    protected static TmdbService getApi() {
        return Api.instance().getApi();
    }

    public static Call<MoviesResponse> loadMovies(String order, String page) {
        return getApi().getMovies(order, BuildConfig.API_KEY, page);
    }

    public static Call<VideosResponse> loadMovieVideos(int movieId) {
        return getApi().getVideos(movieId, BuildConfig.API_KEY);
    }

    public static Call<ReviewsResponse> loadMovieReviews(int movieId, String page) {
        return getApi().getReviews(movieId, BuildConfig.API_KEY, page);
    }
}
