package com.tcc.zeugmaproject.zeugmaproject.Control.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.tcc.zeugmaproject.zeugmaproject.Control.TMDBServices.RetrofitAsyncTasks;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ConteudoAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment  {
    ConteudoAdapter conteudoAdapter;
    private List<Conteudo> conteudoList = new ArrayList<>();
    private int tipo_busca = 3;
    private CheckBox checkFilmes, checkSeries, checkListas ;
    private RecyclerView mRecyclerView1;
    private SearchView searchView;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        mRecyclerView1 = (RecyclerView) view.findViewById(R.id.search_recycler_view);
        checkFilmes = (CheckBox) view.findViewById(R.id.check_filmes);
        checkSeries = (CheckBox) view.findViewById(R.id.check_series);
        checkListas = (CheckBox) view.findViewById(R.id.check_listas);

        mRecyclerView1.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        conteudoAdapter = new ConteudoAdapter(conteudoList, getActivity());
        mRecyclerView1.setAdapter(conteudoAdapter);

        checkListas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Meus favoritos aqui é só fake por enquanto, valeeu!", Toast.LENGTH_LONG).show();
            }
        });

        checkFilmes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configuraFiltros(checkFilmes);
            }
        });

        checkSeries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                configuraFiltros(checkSeries);
            }
        });
//SEARCH VIEW
        searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                conteudoList = new ArrayList<>();
                conteudoAdapter = new ConteudoAdapter(conteudoList, getActivity());
                mRecyclerView1.setAdapter(conteudoAdapter);
                RetrofitAsyncTasks asyncTask1 = new RetrofitAsyncTasks(2, tipo_busca, query, conteudoAdapter, conteudoList);
                asyncTask1.executaParalelo(asyncTask1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
    }

    private void configuraFiltros(CheckBox checkBox){
        if (checkFilmes.isChecked() && (checkSeries.isChecked()))
            tipo_busca = 3;
        else if (checkFilmes.isChecked())
            tipo_busca = 1;
        else if (checkSeries.isChecked())
            tipo_busca = 2;
        else  {
            Toast.makeText(getActivity(), "É necessario ao menos um filtro", Toast.LENGTH_LONG).show();
            if (checkBox.getId() == checkSeries.getId()) {
                checkFilmes.setChecked(true);
                tipo_busca = 1;
            }else {
                checkSeries.setChecked(true);
                tipo_busca =  2;
            }
        }
    }
}
