package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tcc.zeugmaproject.zeugmaproject.Control.TMDBServices.TheMDBServices;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.SeasonAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;
import com.tcc.zeugmaproject.zeugmaproject.Model.Criador;
import com.tcc.zeugmaproject.zeugmaproject.Model.Genre;
import com.tcc.zeugmaproject.zeugmaproject.Model.Network;
import com.tcc.zeugmaproject.zeugmaproject.Model.ProductionCompany;
import com.tcc.zeugmaproject.zeugmaproject.Model.Season;
import com.tcc.zeugmaproject.zeugmaproject.Model.Serie;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SerieDetalhado extends AppCompatActivity {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private TheMDBServices theMDB_service = retrofit.create(TheMDBServices.class);
    private DatabaseReference firabaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference firabaseReferenceListas = FirebaseDatabase.getInstance().getReference();
    private Serie _serie, s = new Serie();
    private Bitmap poster;
    private CheckBox check_favorito, check_assistido;
    private TextView txt_curtidas, txt_descurtidas;
    private Handler handler = new Handler();
    private SeasonAdapter adapter;
    private ListView lista;
    private Conteudo c;
    private ControleConteudo controle = new ControleConteudo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serie_detalhado);
        firabaseReferenceListas = ControleFirebase.getFirebaseReference();

        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            String id = extra.getString("id");
            byte[] byteArray = extra.getByteArray("poster");
            poster = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            lista = (ListView) findViewById(R.id.list_temporadas);
            lista.setFocusable(false);
            getSerie(id);
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        if (adapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < adapter.getCount(); i++) {
            view = adapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void getSerie(String id) {
        final Call<Serie> serie = theMDB_service.getSerieById(id);
        serie.enqueue(new Callback<Serie>() {
            @Override
            public void onResponse(Call<Serie> call, Response<Serie> response) {
                if (response.isSuccessful()){
                    _serie = response.body();
                    adapter = new SeasonAdapter(getApplicationContext(),_serie);
                    lista.setAdapter(adapter);
                    new Thread(){
                        public void run(){
                            try {
                                Bitmap btmp;
                                for (Season s : _serie.getSeasons()) {
                                    if (s.getPoster_path() != null) {
                                        URL url = new URL("https://image.tmdb.org/t/p/w342" + s.getPoster_path());
                                        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                                        InputStream input = conexao.getInputStream();
                                        btmp = (BitmapFactory.decodeStream(input));
                                        _serie.getSeasons().get(_serie.getSeasons().indexOf(s)).setPoster(btmp);

                                        Log.i("aqui meu", "baixou foto");
                                    }
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.notifyDataSetChanged();
                                            setListViewHeightBasedOnChildren(lista);
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                Log.i("Aqui meu - ERRO", "de vdd ao baixar imagem " + e.getMessage());
                            }
                        }
                    }.start();
                    _serie.setBitmap(poster);
                    initView();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String generos = "";
                            String produtoras = "";
                            String idiomas = "";
                            String networks = "";
                            String criadores = "";

                            ImageView img = (ImageView) findViewById(R.id.img_serie_detalhado);
                            img.setImageBitmap(_serie.getBitmap());

                            TextView txt_titulo = (TextView) findViewById(R.id.txt_serie_titulo);
                            TextView txt_status = (TextView) findViewById(R.id.txt_serie_status);
                            TextView txt_temporadas = (TextView) findViewById(R.id.txt_cont_temporadas);
                            TextView txt_episodios = (TextView) findViewById(R.id.txt_cont_episodios);
                            TextView txt_lancamento = (TextView) findViewById(R.id.txt_serie_lancamento);
                            TextView txt_ultima = (TextView) findViewById(R.id.txt_ulitma_data);
                            TextView txt_idioma = (TextView) findViewById(R.id.txt_serie_idioma);
                            TextView txt_duracao = (TextView) findViewById(R.id.txt_serie_duracao);
                            TextView txt_generos = (TextView) findViewById(R.id.txt_serie_generos);
                            TextView txt_produtores = (TextView) findViewById(R.id.txt_serie_produtores);
                            TextView txt_criadores = (TextView) findViewById(R.id.txt_criadores);
                            TextView txt_network = (TextView) findViewById(R.id.txt_network);
                            TextView txt_sinopse = (TextView) findViewById(R.id.txt_serie_sinopse);

                            txt_titulo.setText(_serie.getName());
                            txt_status.setText(txt_status.getText() + "" + _serie.isIn_production());
                            txt_temporadas.setText(txt_temporadas.getText() +"" + _serie.getNumber_of_seasons());
                            txt_episodios.setText(txt_episodios.getText() + "" + _serie.getNumber_of_episodes());
                            txt_lancamento.setText(txt_lancamento.getText() + _serie.getFirst_air_date());
                            txt_ultima.setText(txt_ultima.getText() + _serie.getLast_air_date());
                            txt_duracao.setText(txt_duracao.getText() + "" + _serie.getDuracao() + " minutos");
                            txt_sinopse.setText(_serie.getOverview());

                            for (Genre g : _serie.getGenres()) {
                                generos = generos + g.getName() + "\n                " ;
                            }
                            for (ProductionCompany p : _serie.getProduction_companies()){
                                produtoras = produtoras + p.getName() + "\n                    ";
                            }
                            for (String l : _serie.getLanguages()) {
                                idiomas =  idiomas + l + "\n                ";
                            }
                            for (Criador c : _serie.getCriadores()){
                                criadores = criadores + c.getName() + "\n                    ";
                            }
                            for (Network n : _serie.getNetworks()) {
                                networks = networks + n.getName() + "\n                ";
                            }

                            setListViewHeightBasedOnChildren(lista);
                            txt_generos.setText(txt_generos.getText() + generos);
                            txt_produtores.setText(txt_produtores.getText() + produtoras);
                            txt_idioma.setText(txt_idioma.getText() + idiomas);
                            txt_criadores.setText(txt_criadores.getText() + criadores);
                            txt_network.setText(txt_network.getText() + networks);

                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<Serie> call, Throwable t) {
                Log.i("aqui meu", "errrrou (serie)");
                Log.i("aqui meu", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void initView(){
        TextView txt_curtir = (TextView) findViewById(R.id.txt_serie_gostei);
        TextView txt_descurtir = (TextView) findViewById(R.id.txt_serie_ngostei);
        check_favorito = (CheckBox) findViewById(R.id.check_serie_favorito);
        check_assistido = (CheckBox) findViewById(R.id.check_serie_assistido);
        txt_curtidas = (TextView) findViewById(R.id.txt_serie_curtidas);
        txt_descurtidas = (TextView) findViewById(R.id.txt_serie_descurtidas);
//CARREGA LIKES DISLIKES
        firabaseReference.child("serie").child(_serie.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            s = dataSnapshot.getValue(Serie.class);
                            if (s != null){
                                _serie.setDescurtidas(s.getDescurtidas());
                                _serie.setCurtidas(s.getCurtidas());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        txt_curtidas.setText(Integer.toString(_serie.getCurtidas()));
                                        txt_descurtidas.setText(Integer.toString(_serie.getDescurtidas()));
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
        firabaseReferenceListas.child("assistidos").child("serie").
                child(_serie.getId()).child(ControleFirebase.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        firabaseReferenceListas.child("favoritos").child("serie").
                child(_serie.getId()).child(ControleFirebase.getUserID()).
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

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (_serie.getNumber_of_episodes() > 0) {
                    Intent intent = new Intent(SerieDetalhado.this, ListaEpisodios.class);
                    Season season_aux = (Season) adapter.getItem(position);
                    intent.putExtra("id", _serie.getId());
                    intent.putExtra("temporada", Integer.toString(season_aux.getSeason_number()));
                    intent.putExtra("backdrop", _serie.getBackdrop_path());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Nenhuma informção disponivel", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void preencheConteudo(){
        c = new Conteudo();
        c.setId(_serie.getId());
        c.setTipo(_serie.getTipo());
        c.setPoster_path(_serie.getPoster_path());
        int cont = 0, soma = 0;
        for (int i : _serie.getDuracao()){
            soma = soma + i;
            cont++;
        }
        c.setRuntime(soma/cont);
    }

}
