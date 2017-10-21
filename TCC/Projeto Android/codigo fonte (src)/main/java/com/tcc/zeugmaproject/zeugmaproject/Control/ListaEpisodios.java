package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.tcc.zeugmaproject.zeugmaproject.Control.TMDBServices.TheMDBServices;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.EpisodiosAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Episode;
import com.tcc.zeugmaproject.zeugmaproject.Model.Season;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ListaEpisodios extends AppCompatActivity {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private TheMDBServices theMDB_Services = retrofit.create(TheMDBServices.class);
    private EpisodiosAdapter episodiosAdapter;
    private RecyclerView mRecyclerView;
    private Handler handler = new Handler();
    private Season _season;
    private String backdrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodios);

        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(ListaEpisodios.this, LinearLayoutManager.VERTICAL, false);
            mRecyclerView = (RecyclerView) findViewById(R.id.receycler_episodios);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setHasFixedSize(false);
            backdrop = extra.getString("backdrop");
            getEpisodes(extra.getString("id"), extra.getString("temporada"));
        }
    }


    public void getEpisodes(String id, String temporada){
        final Call<Season> season = theMDB_Services.getEpisodes(id, temporada);
        season.enqueue(new Callback<Season>() {
            @Override
            public void onResponse(Call<Season> call, Response<Season> response) {
                if (response.isSuccessful()) {
                    _season = response.body();
                    episodiosAdapter = new EpisodiosAdapter(_season, getApplicationContext());
                    mRecyclerView.setAdapter(episodiosAdapter);
                    new Thread(){
                        public void run() {
                            Bitmap btmp;
                            URL url;
                            try {
                                url = new URL("http://image.tmdb.org/t/p/w780" + backdrop);
                                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                                InputStream input = conexao.getInputStream();
                                btmp = (BitmapFactory.decodeStream(input));
                                _season.setBackdrop(btmp);
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.i("Aqui meu - ERRO", "erro ao baixar backdrop: http://image.tmdb.org/t/p/w342" + backdrop);
                            }
                            for (Episode ep : _season.getEpisodes()) {
                                try {

                                    if (ep.getStill_path() != null) {
                                        url = new URL("http://image.tmdb.org/t/p/w780" + ep.getStill_path());
                                        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                                        InputStream input = conexao.getInputStream();
                                        btmp = (BitmapFactory.decodeStream(input));
                                        _season.getEpisodes().get(_season.getEpisodes().indexOf(ep)).setPoster(btmp);
                                    }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    episodiosAdapter.notifyDataSetChanged();
                                }
                            });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.i("Aqui meu - ERRO", "erro ao baixar imagens episodios: http://image.tmdb.org/t/p/w342" + ep.getStill_path() + "\n ");
                                }
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void onFailure(Call<Season> call, Throwable t) {

            }
        });
    }

}
