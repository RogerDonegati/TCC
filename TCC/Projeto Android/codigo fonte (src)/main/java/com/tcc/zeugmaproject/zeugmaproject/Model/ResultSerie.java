package com.tcc.zeugmaproject.zeugmaproject.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultSerie {
    @SerializedName("results")
    private List<Serie> series;

    public List<Serie> getSeries() {
        return series;
    }

    public void setSeries(List<Serie> series) {
        this.series = series;
    }
}