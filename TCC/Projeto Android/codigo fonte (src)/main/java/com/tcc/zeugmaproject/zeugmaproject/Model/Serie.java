package com.tcc.zeugmaproject.zeugmaproject.Model;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Serie extends Conteudo {

    private int  number_of_seasons, number_of_episodes;
    private String backdrop_path, original_name, name;
    private String last_air_date, first_air_date;
    private boolean in_production;
    private List<Season> seasons;
    private List<Network> networks;
    private List<String> languages;
    private Bitmap backdrop;

    @SerializedName("created_by")
    private List<Criador> criadores;

    @SerializedName("episode_run_time")
    private List<Integer> duracao;

    public Serie() {
        setTipo(2);
    }

    public int getNumber_of_seasons() {
        return number_of_seasons;
    }

    public String getLast_air_date() {
        if ((last_air_date == null) || (last_air_date.isEmpty()))
            return "";
        else {
            String data_inicial = last_air_date;
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

    public void setLast_air_date(String last_air_date) {
        this.last_air_date = last_air_date;
    }

    public String getFirst_air_date() {
        if ((first_air_date == null) || (first_air_date.isEmpty())){
            return "";
        }else {
            String data_inicial = first_air_date;
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

    public void setFirst_air_date(String first_air_date) {
        this.first_air_date = first_air_date;
    }

    public void setNumber_of_seasons(int number_of_seasons) {
        this.number_of_seasons = number_of_seasons;
    }

    public int getNumber_of_episodes() {
        return number_of_episodes;
    }

    public void setNumber_of_episodes(int number_of_episodes) {
        this.number_of_episodes = number_of_episodes;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIn_production() {
        return in_production;
    }

    public void setIn_production(boolean in_production) {
        this.in_production = in_production;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public void setNetworks(List<Network> networks) {
        this.networks = networks;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Bitmap getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(Bitmap backdrop) {
        this.backdrop = backdrop;
    }

    public List<Criador> getCriadores() {
        return criadores;
    }

    public void setCriadores(List<Criador> criadores) {
        this.criadores = criadores;
    }

    public List<Integer> getDuracao() {
        return duracao;
    }

    public void setDuracao(List<Integer> duracao) {
        this.duracao = duracao;
    }


}