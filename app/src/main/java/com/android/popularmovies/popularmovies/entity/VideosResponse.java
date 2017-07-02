package com.android.popularmovies.popularmovies.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lei on 6/27/17.
 */

public class VideosResponse {

    @SerializedName("id")
    private int id;
    @SerializedName("results")
    private List<Video> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
}
