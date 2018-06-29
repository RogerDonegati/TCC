package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ConteudoAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;
import com.tcc.zeugmaproject.zeugmaproject.Model.Movie;
import com.tcc.zeugmaproject.zeugmaproject.Model.Serie;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MostraListasActivity extends AppCompatActivity {

    ConteudoAdapter conteudoAdapter;
    private List<Conteudo> conteudoList = new ArrayList<>();
    private String lista;
    private CheckBox checkFilmes, checkSeries;
    private List<Conteudo> movieList = new ArrayList<>();
    private List<Conteudo> serieList = new ArrayList<>();
    private RecyclerView mRecyclerView1;
    private Handler handler = new Handler();
    private DatabaseReference firabaseReferenceListas;
    private Conteudo c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_listas);


        checkFilmes = (CheckBox) findViewById(R.id.listas_check_filmes);
        checkSeries = (CheckBox) findViewById(R.id.listas_check_series);
        firabaseReferenceListas = ControleFirebase.getFirebaseReference();
        mRecyclerView1 = (RecyclerView) findViewById(R.id.listas_recycler_view);

        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            mRecyclerView1.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        else
            mRecyclerView1.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            TextView titulo = (TextView) findViewById(R.id.txt_mostra_listas_titulo);
            titulo.setText(extra.getString("titulo"));
            lista = extra.getString("lista");
        }


        checkFilmes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                carregaLista(configuraFiltros(checkFilmes));
            }
        });

        checkSeries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                carregaLista(configuraFiltros(checkSeries));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista(configuraFiltros(checkFilmes));
    }

    private int configuraFiltros(CheckBox checkBox){
        if (checkFilmes.isChecked() && (checkSeries.isChecked()))
            return 3;
        else if (checkFilmes.isChecked())
            return  1;
        else if (checkSeries.isChecked())
            return  2;
        else  {
            Toast.makeText(getApplicationContext(), "É necessario ao menos um filtro", Toast.LENGTH_LONG).show();
            if (checkBox.getId() == checkSeries.getId()) {
                checkFilmes.setChecked(true);
                return 1;
            }else {
                checkSeries.setChecked(true);
                return 2;
            }
        }
    }

    private void carregaLista(final int tipo) {
        new Thread() {
            public void run() {
                DatabaseReference firabaseAux = firabaseReferenceListas;
                if ((tipo == 1) || (tipo == 2)) {
                    if (tipo == 1)
                        firabaseAux = firabaseReferenceListas.child(lista).child("filme");
                    else
                        firabaseAux = firabaseReferenceListas.child(lista).child("serie");
                    firabaseAux.orderByChild(ControleFirebase.getUserID()).equalTo(true).addListenerForSingleValueEvent
                            (new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            conteudoList = new ArrayList<>();
                            conteudoAdapter = new ConteudoAdapter(conteudoList, getApplicationContext());
                            mRecyclerView1.setAdapter(conteudoAdapter);
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //c = postSnapshot.getValue(Conteudo.class);
                                if (postSnapshot != null) {
                                    c = new Conteudo();
                                    c.setId(postSnapshot.getKey());
                                    c.setPoster_path(postSnapshot.child("poster_path").getValue().toString());
                                    c.setTipo(tipo);
                                    try {
                                        Bitmap btmp;
                                        URL url = new URL("http://image.tmdb.org/t/p/w342" + c.getPoster_path());
                                        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                                        InputStream input = conexao.getInputStream();
                                        btmp = (BitmapFactory.decodeStream(input));
                                        c.setBitmap(btmp);
                                        conteudoList.add(c);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                conteudoAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.i("Aqui meu - ERRO", "erro ao baixar imagens mostra lista activity: " + c.getPoster_path() +
                                                "\n " + e.getMessage());
                                    }
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if (tipo == 3) {
                    firabaseAux.child(lista).child("filme").orderByChild(ControleFirebase.getUserID()).equalTo(true).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            movieList = new ArrayList<>();
                            DatabaseReference firabaseAux2 = firabaseReferenceListas;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if (postSnapshot != null) {
                                    Movie m = new Movie();
                                    m.setId(postSnapshot.getKey());
                                    m.setPoster_path(postSnapshot.child("poster_path").getValue().toString());
                                    movieList.add(m);
                                }
                            }
                            firabaseAux2.child(lista).child("serie").orderByChild(ControleFirebase.getUserID()).equalTo(true).
                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int i = 0;
                                    serieList = new ArrayList<>();
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (postSnapshot != null) {
                                            Serie s = new Serie();
                                            s.setId(postSnapshot.getKey());
                                            s.setPoster_path(postSnapshot.child("poster_path").getValue().toString());
                                            serieList.add(s);
                                        }
                                    }
                                    mRecyclerView1 = (RecyclerView) findViewById(R.id.listas_recycler_view);
                                    mRecyclerView1.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                                    conteudoList = new ArrayList<>();
                                    conteudoAdapter = new ConteudoAdapter(conteudoList, getApplicationContext());
                                    mRecyclerView1.setAdapter(conteudoAdapter);
                                    while ((movieList.size() > i) || (serieList.size() > i)) {
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
                                                conteudoList.add(c);

                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        conteudoAdapter.notifyDataSetChanged();
                                                    }
                                                });
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
                                                c.setTipo(serieList.get(i).getTipo());
                                                c.setId(serieList.get(i).getId());
                                                conteudoList.add(c);
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        conteudoAdapter.notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        }
                                        i = i + 1;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        }.start();
    }
}
