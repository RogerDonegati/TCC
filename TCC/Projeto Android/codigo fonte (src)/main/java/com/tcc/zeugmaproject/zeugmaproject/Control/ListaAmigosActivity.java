package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.tcc.zeugmaproject.zeugmaproject.Control.TMDBServices.RetrofitAsyncTasks;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ConteudoAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Usuario;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.util.ArrayList;
import java.util.List;

public class ListaAmigosActivity extends AppCompatActivity {

    private List<Usuario> usuarioList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_amigos);

        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.receycler_lista_amigos);
        SearchView searchView = (SearchView) findViewById(R.id.searchview_lista_amigos);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                usuarioList = new ArrayList<>();
//                conteudoAdapter = new ConteudoAdapter(usuarioList, getApplicationContext());
//                mRecyclerView.setAdapter(conteudoAdapter);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }
}
