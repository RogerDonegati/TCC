package com.tcc.zeugmaproject.zeugmaproject.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultMovie {

    @SerializedName("results")
    private List<Movie> movies;

    public List<Movie> getMovies() {
        return movies;
    }

}