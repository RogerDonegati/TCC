package com.tcc.zeugmaproject.zeugmaproject.Control.TMDBServices;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ConteudoAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ImagemAdapatdor;
import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;
import com.tcc.zeugmaproject.zeugmaproject.Model.Movie;
import com.tcc.zeugmaproject.zeugmaproject.Model.ResultMovie;
import com.tcc.zeugmaproject.zeugmaproject.Model.ResultSerie;
import com.tcc.zeugmaproject.zeugmaproject.Model.Serie;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAsyncTasks extends AsyncTask<String, Integer, String> {
    private String busca;
    String argumento;
    ConteudoAdapter conteudoAdapter;
    ImagemAdapatdor imagem_adapter;
    int carregados ,carregar;

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'.")
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    private TheMDBServices theMDBServices = retrofit.create(TheMDBServices.class);
   // theMDBServices.getMovieByID(ID);
    private List<Conteudo> conteudosList = new ArrayList<>();
    private int identificador, acao, tipo_busca;

    //MAIN THREAD
    private Handler handler = new Handler();


    public RetrofitAsyncTasks(int acao, String menu, int identificador, int carregar, ConteudoAdapter conteudoAdapter,
                              ImagemAdapatdor imagem_adapter, List<Conteudo> conteudosList) {
        this.argumento = menu;
        this.acao = acao;
        this.conteudoAdapter = conteudoAdapter;
        this.imagem_adapter = imagem_adapter;
        this.conteudosList = conteudosList;
        this.identificador = identificador;
        this.carregados = 0;
        this.carregar = carregar;
    }

    public RetrofitAsyncTasks(int acao, int tipo_busca, String busca, ConteudoAdapter conteudoAdapter, List<Conteudo> conteudoList) {
        this.acao = acao;
        this.busca = busca;
        this.conteudoAdapter = conteudoAdapter;
        this.conteudosList = conteudoList;
        this.tipo_busca = tipo_busca;
    }

    @Override
    protected String doInBackground(String... urls) {
        if (acao == 1)
          carregaMenus();
        else if (acao == 2)
            ProcuraConteudos(tipo_busca);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    @Override
    protected void onPostExecute(String result) {
}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void executaParalelo(RetrofitAsyncTasks task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }

    private void carregaMenus(){
        int pg = 1;
        if (identificador == 1) {
            while (carregados < carregar) {
                final Call<ResultMovie> conteudo = theMDBServices.getMovieLists(argumento, pg);
                try {
                    Response<ResultMovie> response = conteudo.execute();
                    ResultMovie conteudos = response.body();
                    for (Movie m : conteudos.getMovies()) {
                        if (m.getPoster_path() != null) {
                            // baixaImagens(conteudos.getMovies());
                            try {
                                Bitmap btmp;
                                URL url = new URL("http://image.tmdb.org/t/p/w342" + m.getPoster_path());
                                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

                                InputStream input = conexao.getInputStream();
                                btmp = (BitmapFactory.decodeStream(input));
                                Conteudo c = new Conteudo();
                                c.setBitmap(btmp);
                                c.setTitulo(m.getTitulo());
                                c.setTipo(m.getTipo());
                                c.setId(m.getId());
                                conteudosList.add(c);
                                atualizaAdapters();
                                carregados = carregados +1;
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.i("Aqui meu - ERRO", "erro ao baixar imagens filmes: " + m.getPoster_path() + "\n " + e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Aqui meu - ERRO", "erro ao baixar filmes/series: " + "\n " + e.getMessage());
                }
                pg = pg + 1;
            }
        } else {
            while (carregados < carregar) {
                final Call<ResultSerie> conteudo = theMDBServices.getSerieList(argumento, pg);
                try {
                    Response<ResultSerie> response = conteudo.execute();
                    ResultSerie conteudos = response.body();
                    for (Serie s : conteudos.getSeries()) {
                        if (s.getPoster_path() != null) {
                            try {
                                Bitmap btmp;
                                URL url = new URL("http://image.tmdb.org/t/p/w342" + s.getPoster_path());
                                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                                InputStream input = conexao.getInputStream();
                                btmp = (BitmapFactory.decodeStream(input));
                                Conteudo c = new Conteudo();
                                c.setBitmap(btmp);
                                c.setTitulo(s.getName());
                                c.setTipo(s.getTipo());
                                c.setId(s.getId());
                                conteudosList.add(c);
                                atualizaAdapters();
                                carregados = carregados +1;
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.i("Aqui meu - ERRO", "erro ao baixar imagens series: " + s.getPoster_path() + "\n " + e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("Aqui meu - ERRO", "erro ao baixar filmes/series: " + "\n " + e.getMessage());
                }
                pg = pg + 1;
            }
        }
    }

    private void ProcuraConteudos(int tipo_busca) {

        List<Serie> serieList = new ArrayList<>();;
        List<Movie> movieList = new ArrayList<>();;
        try {
            if ((tipo_busca == 1) || (tipo_busca == 3)) {
                final Call<ResultMovie> conteudo1 = theMDBServices.getMovieSearchByName(busca);
                Response<ResultMovie> response1 = conteudo1.execute();
                ResultMovie conteudos1 = response1.body();
                movieList = conteudos1.getMovies();
            }
            if ((tipo_busca == 2) || (tipo_busca == 3)) {
                final Call<ResultSerie> conteudo2 = theMDBServices.getSerieSearchByName(busca);
                Response<ResultSerie> response2 = conteudo2.execute();
                ResultSerie conteudos2 = response2.body();
                serieList = conteudos2.getSeries();
            }

            if (movieList == null)
                movieList = new ArrayList<>();
            if (serieList == null)
                serieList = new ArrayList<>();
            int i = 0;
            while ((movieList.size() > i ) || (serieList.size() > i)) {
                if (movieList.size() > i) {
                    if (movieList.get(i).getPoster_path() != null) {
                        try {
                            Bitmap btmp;
                            URL url = new URL("http://image.tmdb.org/t/p/w342" + movieList.get(i).getPoster_path());
                            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

                            InputStream input = conexao.getInputStream();
                            btmp = (BitmapFactory.decodeStream(input));
                            movieList.get(i).setBitmap(btmp);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i("Aqui meu - ERRO", "erro ao baixar imagens filmes: " + movieList.get(i).getPoster_path() + "\n " + e.getMessage());
                        }
                        Conteudo c = new Conteudo();
                        c.setBitmap(movieList.get(i).getBitmap());
                        c.setTitulo(movieList.get(i).getTitulo());
                        c.setTipo(movieList.get(i).getTipo());
                        c.setId(movieList.get(i).getId());
                        conteudosList.add(c);
                        atualizaAdapters();
                    }
                }
                if (serieList.size() > i) {
                    if (serieList.get(i).getPoster_path() != null) {
                        try {
                            Bitmap btmp;
                            URL url = new URL("http://image.tmdb.org/t/p/w342" + serieList.get(i).getPoster_path());
                            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                            InputStream input = conexao.getInputStream();
                            btmp = (BitmapFactory.decodeStream(input));
                            serieList.get(i).setBitmap(btmp);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i("Aqui meu - ERRO", "erro ao baixar imagens series: " + serieList.get(i).getPoster_path() + "\n " + e.getMessage());
                        }
                    Conteudo c = new Conteudo();
                    c.setBitmap(serieList.get(i).getBitmap());
                    c.setTitulo(serieList.get(i).getName());
                    c.setTipo(serieList.get(i).getTipo());
                    c.setId(serieList.get(i).getId());
                    conteudosList.add(c);
                    atualizaAdapters();
                    }
                }
                i = i + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Aqui meu - ERRO", "erro ao filtrar filmes/series: " + "\n " + e.getMessage());
        }
    }

    private void atualizaAdapters(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (conteudoAdapter != null)
                    conteudoAdapter.notifyDataSetChanged();
                else
                    imagem_adapter.notifyDataSetChanged();
            }
        });
    }
}
