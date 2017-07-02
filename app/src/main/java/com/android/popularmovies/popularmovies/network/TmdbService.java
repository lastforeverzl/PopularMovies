package com.android.popularmovies.popularmovies.network;

import com.android.popularmovies.popularmovies.entity.MoviesResponse;
import com.android.popularmovies.popularmovies.entity.ReviewsResponse;
import com.android.popularmovies.popularmovies.entity.VideosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lei on 6/27/17.
 */

public interface TmdbService {

    @GET("movie/popular")
    Call<MoviesResponse> getPopular(@Query("api_key") String apiKey,
                                    @Query("page") String page);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRated(@Query("api_key") String apiKey,
                                     @Query("page") String page);

    @GET("movie/{id}/videos")
    Call<VideosResponse> getVideos(@Path("id") int movieId,
                                   @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewsResponse> getReviews(@Path("id") int movieId,
                                     @Query("api_key") String apiKey,
                                     @Query("page") String page);
}
