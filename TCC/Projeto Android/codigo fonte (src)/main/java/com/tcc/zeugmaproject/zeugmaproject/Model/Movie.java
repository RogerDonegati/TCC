package com.tcc.zeugmaproject.zeugmaproject.Model;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Movie extends Conteudo {

    private String original_language, status;
    private Video videos;
    private long revenue, budget;
    private String release_date;

    public Movie() {
        setTipo(1);
    }

    public String getRelease_date() {
        if ((release_date == null) || (release_date.isEmpty())){
            return  "";
        }else {
            String data_inicial = release_date;
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            Date data = null;
            try {
                data = formato.parse(data_inicial);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.i("Aqui meu", "erro ao converter data");
            }
            formato.applyPattern("dd/MM/yyyy");
            String dataFormatada;
            dataFormatada = formato.format(data);
            return dataFormatada;
        }
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOriginal_language() {
        if ((original_language != null) && (original_language.equals("en"))){
                original_language = "Inglês";
        }
        return original_language;
    }

    public String getStatus() {
        if (status != null && status.equals("Released"))
            status = "Lançado";
        else
            status = "Não Lançado";
        return status;
    }

    public Video getVideos() {
        return videos;
    }

    public void setVideos(Video videos) {
        this.videos = videos;
    }

    public void setBudget(int budget) {

        this.budget = budget;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public long getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    public long getBudget() {
        return budget;
    }


}