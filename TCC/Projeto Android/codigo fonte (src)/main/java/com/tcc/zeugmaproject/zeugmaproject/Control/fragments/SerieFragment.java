package com.tcc.zeugmaproject.zeugmaproject.Control.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcc.zeugmaproject.zeugmaproject.Control.ListaConteudo;
import com.tcc.zeugmaproject.zeugmaproject.Control.TMDBServices.RetrofitAsyncTasks;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ConteudoAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SerieFragment extends Fragment {

    public SerieFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_serie, container, false);
        RecyclerView mRecyclerView5 = (RecyclerView) view.findViewById(R.id.horizontal_recycler_view5);
        RecyclerView mRecyclerView6 = (RecyclerView) view.findViewById(R.id.horizontal_recycler_view6);

        LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView5.setLayoutManager(horizontalLayoutManager1);
        mRecyclerView6.setLayoutManager(horizontalLayoutManager2);
        List<Conteudo> conteudoList1 = new ArrayList<>();
        List<Conteudo> conteudoList2 = new ArrayList<>();


        ConteudoAdapter conteudoAdapter1 = new ConteudoAdapter(conteudoList1, getActivity());
        ConteudoAdapter conteudoAdapter2 = new ConteudoAdapter(conteudoList2, getActivity());

        mRecyclerView5.setAdapter(conteudoAdapter1);
        mRecyclerView6.setAdapter(conteudoAdapter2);

        if ((savedInstanceState == null) || (!savedInstanceState.isEmpty())){
            Log.i("aqui meu", "recarregando dados series");
            RetrofitAsyncTasks asyncTask1 = new RetrofitAsyncTasks(1, "popular", 2, 19, conteudoAdapter1, null, conteudoList1);
            asyncTask1.executaParalelo(asyncTask1);
            RetrofitAsyncTasks asyncTask2 = new RetrofitAsyncTasks(1, "top_rated", 2, 19, conteudoAdapter2, null, conteudoList2);
            asyncTask2.executaParalelo(asyncTask2);
        }

//        TextView subtitulo5 = (TextView) view.findViewById(R.id.SubTitulo5);
//        TextView subtitulo6 = (TextView) view.findViewById(R.id.SubTitulo6);
//        subtitulo5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ListaConteudo.class);
//                intent.putExtra("menu", "popular");
//                intent.putExtra("titulo", "Series Populares");
//                intent.putExtra("tipo", 2);
//                startActivity(intent);
//            }
//        });
//
//        subtitulo6.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ListaConteudo.class);
//                intent.putExtra("menu", "top_rated");
//                intent.putExtra("titulo", "Melhor Avaliadas");
//                intent.putExtra("tipo", 2);
//                startActivity(intent);
//            }
//        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setRetainInstance(true);
    }
}
