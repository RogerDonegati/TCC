package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tcc.zeugmaproject.zeugmaproject.Control.TMDBServices.TheMDBServices;
import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;
import com.tcc.zeugmaproject.zeugmaproject.Model.Genre;
import com.tcc.zeugmaproject.zeugmaproject.Model.Movie;
import com.tcc.zeugmaproject.zeugmaproject.Model.ProductionCompany;
import com.tcc.zeugmaproject.zeugmaproject.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetalhado extends AppCompatActivity {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    private TheMDBServices theMDBServices = retrofit.create(TheMDBServices.class);
    private Movie _movie, m;
    private Conteudo c;
    private Bitmap poster;
    private Handler handler = new Handler();
    private CheckBox check_favorito, check_assistido;
    private TextView txt_curtidas, txt_descurtidas;
    private ControleConteudo controle = new ControleConteudo();
    private DatabaseReference firabaseReferenceListas, firabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detalhado);
        firabaseReferenceListas = ControleFirebase.getFirebaseReference();
        firabaseReference = ControleFirebase.getFirebaseReference();

        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            String id = extra.getString("id");
            byte[] byteArray = extra.getByteArray("poster");
            poster = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Log.i("Aqui meu", "Chamando get movie" );
            getMovie(id);
      }
    }

    protected void onPause(){
        super.onPause();
    }

    public void getMovie(String id){
        final Call<Movie> movie = theMDBServices.getMovieById(id);
        movie.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()){
                    _movie = response.body();
                    //ativa listeners agora que tem o filme carregado
                    initView();
                    _movie.setBitmap(poster);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String generos = "";
                            String produtoras = "";
//CRIA REFERENCIAS
                            ImageView img = (ImageView) findViewById(R.id.img_movie_detalhado);
                            TextView txt_titulo = (TextView) findViewById(R.id.txt_movie_titulo);
                            TextView txt_status = (TextView) findViewById(R.id.txt_movie_status);
                            TextView txt_lancamento = (TextView) findViewById(R.id.txt_movie_lancamento);
                            TextView txt_duracao = (TextView) findViewById(R.id.txt_movie_duracao);
                            TextView txt_idioma = (TextView) findViewById(R.id.txt_movie_idioma);
                            TextView txt_generos = (TextView) findViewById(R.id.txt_movie_genero);
                            TextView txt_produtores = (TextView) findViewById(R.id.txt_movie_produtora);
                            TextView txt_orcamento = (TextView) findViewById(R.id.txt_movie_orcamento);
                            TextView txt_receita = (TextView) findViewById(R.id.txt_movie_receita);
                            TextView txt_sinopse = (TextView) findViewById(R.id.txt_movie_sinopse);
//PREECNHE DADOS
                            txt_titulo.setText(_movie.getTitulo());
                            img.setImageBitmap(_movie.getBitmap());
                            txt_status.setText(txt_status.getText() + _movie.getStatus());
                            txt_lancamento.setText(txt_lancamento.getText() + _movie.getRelease_date());
                            txt_duracao.setText(txt_duracao.getText() + "" + _movie.getRuntime() + " minutos");
                            txt_idioma.setText(txt_idioma.getText() + _movie.getOriginal_language());
                            txt_orcamento.setText(txt_orcamento.getText() + "" + _movie.getBudget());
                            txt_receita.setText(txt_receita.getText() + "" + _movie.getRevenue());
                            txt_sinopse.setText(_movie.getOverview());

                            for (Genre g : _movie.getGenres()) {
                                generos = generos  + g.getName() + "\n                ";
                            }

                            for (ProductionCompany p : _movie.getProduction_companies()) {
                                produtoras = produtoras  + p.getName() + "\n                    ";
                            }
                            txt_generos.setText(txt_generos.getText() + generos);
                            txt_produtores.setText(txt_produtores.getText() + produtoras);
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.i("aqui meu", "errrrou (movie)");
                Log.i("aqui meu", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public void initView(){
        TextView txt_curtir = (TextView) findViewById(R.id.txt_movie_gostei);
        TextView txt_descurtir = (TextView) findViewById(R.id.txt_movie_ngostei);

        check_favorito = (CheckBox) findViewById(R.id.check_movie_favorito);
        check_assistido = (CheckBox) findViewById(R.id.check_movie_assistido);
        txt_curtidas = (TextView) findViewById(R.id.txt_movie_curtidas);
        txt_descurtidas = (TextView) findViewById(R.id.txt_movie_descurtidas);

//CARREGA LIKES DISLIKES
        firabaseReference.child("filme").child(_movie.getId()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            m = dataSnapshot.getValue(Movie.class);
                            if (m != null){
                                _movie.setDescurtidas(m.getDescurtidas());
                                _movie.setCurtidas(m.getCurtidas());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        txt_curtidas.setText(Integer.toString(_movie.getCurtidas()));
                                        txt_descurtidas.setText(Integer.toString(_movie.getDescurtidas()));
                                    }
                                });
                            }
                        }catch (Exception e){
                            Log.i("aqui meu ", "erro get movie: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//VERIFICA ASSISTIDO
        firabaseReference.child("assistidos").child("filme").
                child(_movie.getId()).child(ControleFirebase.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null){
                        check_assistido.setChecked(true);
                    }
                }catch (Exception e){
                    Log.i("aqui meu ", "erro get movie: " + e.getMessage());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//VERIFICA FAVORITO
        firabaseReferenceListas.child("favoritos").child("filme").child(_movie.getId()).child(ControleFirebase.getUserID()).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.getValue() != null){
                        check_favorito.setChecked(true);
                    }
                }catch (Exception e){
                    Log.i("aqui meu ", "erro get movie: " + e.getMessage());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        preencheConteudo();

        txt_curtir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controle.curtir(c);
            }
        });

        txt_descurtir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controle.descurtir(c);
            }
        });

        check_assistido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_assistido.isChecked())
                    controle.adicionaAssistido(c);
                else
                    controle.removeAssitido(c);
            }
        });
        check_favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_favorito.isChecked())
                    controle.adicionaFavorito(c);
                else
                    controle.removeFavorito(c);
            }
        });

    }

    public void preencheConteudo(){
        c = new Conteudo();
        c.setId(_movie.getId());
        c.setTipo(_movie.getTipo());
        c.setPoster_path(_movie.getPoster_path());
        c.setRuntime(_movie.getRuntime());
    }

}
