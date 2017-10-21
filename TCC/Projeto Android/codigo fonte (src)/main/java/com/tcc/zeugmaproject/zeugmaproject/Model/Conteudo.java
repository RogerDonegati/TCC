package com.tcc.zeugmaproject.zeugmaproject.Model;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Conteudo {
    private String id;
    private String overview;
    private String poster_path;
    private List<ProductionCompany> production_companies;
    private List<Genre> genres;
    private Bitmap bitmap;
    private int tipo;
    private int runtime;
    private int curtidas, descurtidas;


    @SerializedName("title")
    private String titulo;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    @Exclude
    public int getTipo() {
        return tipo;
    }
    @Exclude
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public List<ProductionCompany> getProduction_companies() {
        return production_companies;
    }

    public void setProduction_companies(List<ProductionCompany> production_companies) {
        this.production_companies = production_companies;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Exclude
    public int getRuntime() {
        return runtime;
    }

    @Exclude
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public int getCurtidas() {
        return curtidas;
    }

    public void setCurtidas(int curtidas) {
        this.curtidas = curtidas;
    }

    public int getDescurtidas() {
        return descurtidas;
    }

    public void setDescurtidas(int descurtidas) {
        this.descurtidas = descurtidas;
    }
}