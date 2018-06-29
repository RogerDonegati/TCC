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
public class MovieFragment extends Fragment {

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_movie, container, false);
        RecyclerView mRecyclerView1 = (RecyclerView) view.findViewById(R.id.horizontal_recycler_view1);
        RecyclerView mRecyclerView2 = (RecyclerView) view.findViewById(R.id.horizontal_recycler_view2);
        RecyclerView mRecyclerView3 = (RecyclerView) view.findViewById(R.id.horizontal_recycler_view3);
        RecyclerView mRecyclerView4 = (RecyclerView) view.findViewById(R.id.horizontal_recycler_view4);

        LinearLayoutManager horizontalLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager horizontalLayoutManager4 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);


        mRecyclerView1.setLayoutManager(horizontalLayoutManager1);
        mRecyclerView2.setLayoutManager(horizontalLayoutManager2);
        mRecyclerView3.setLayoutManager(horizontalLayoutManager3);
        mRecyclerView4.setLayoutManager(horizontalLayoutManager4);

        List<Conteudo> conteudoList1 = new ArrayList<>();
        List<Conteudo> conteudoList2 = new ArrayList<>();
        List<Conteudo> conteudoList3 = new ArrayList<>();
        List<Conteudo> conteudoList4 = new ArrayList<>();

        ConteudoAdapter conteudoAdapter1 = new ConteudoAdapter(conteudoList1, getActivity());
        ConteudoAdapter conteudoAdapter2 = new ConteudoAdapter(conteudoList2, getActivity());
        ConteudoAdapter conteudoAdapter3 = new ConteudoAdapter(conteudoList3, getActivity());
        ConteudoAdapter conteudoAdapter4 = new ConteudoAdapter(conteudoList4, getActivity());

        mRecyclerView1.setAdapter(conteudoAdapter1);
        mRecyclerView2.setAdapter(conteudoAdapter2);
        mRecyclerView3.setAdapter(conteudoAdapter3);
        mRecyclerView4.setAdapter(conteudoAdapter4);
        if ((savedInstanceState == null) || (!savedInstanceState.isEmpty())){
            Log.i("aqui meu", "recarregando dados movie");
            RetrofitAsyncTasks asyncTask1 = new RetrofitAsyncTasks(1, "popular", 1, 15, conteudoAdapter1, null, conteudoList1);
            asyncTask1.executaParalelo(asyncTask1);
            RetrofitAsyncTasks asyncTask2 = new RetrofitAsyncTasks(1, "top_rated", 1, 15, conteudoAdapter2, null, conteudoList2);
            asyncTask2.executaParalelo(asyncTask2);
            RetrofitAsyncTasks asyncTask3 = new RetrofitAsyncTasks(1, "upcoming", 1, 15, conteudoAdapter3, null, conteudoList3);
            asyncTask3.executaParalelo(asyncTask3);
            RetrofitAsyncTasks asyncTask4 = new RetrofitAsyncTasks(1, "now_playing", 1, 15, conteudoAdapter4, null, conteudoList4);
            asyncTask4.executaParalelo(asyncTask4);
        }
//        TextView subtitulo1 = (TextView) view.findViewById(R.id.SubTitulo1);
//        TextView subtitulo2 = (TextView) view.findViewById(R.id.SubTitulo2);
//        TextView subtitulo3 = (TextView) view.findViewById(R.id.SubTitulo3);
//        TextView subtitulo4 = (TextView) view.findViewById(R.id.SubTitulo4);
//
//        subtitulo1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ListaConteudo.class);
//                intent.putExtra("menu", "popular");
//                intent.putExtra("titulo", "Populares");
//                intent.putExtra("tipo", 1);
//                startActivity(intent);
//            }
//        });
//
//        subtitulo2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ListaConteudo.class);
//                intent.putExtra("menu", "top_rated");
//                intent.putExtra("titulo", "Melhor Avaliados");
//                intent.putExtra("tipo", 1);
//                startActivity(intent);
//            }
//        });
//
//        subtitulo3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ListaConteudo.class);
//                intent.putExtra("menu", "upcoming");
//                intent.putExtra("titulo", "Lançamentos");
//                intent.putExtra("tipo", 1);
//                startActivity(intent);
//            }
//        });
//
//        subtitulo4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ListaConteudo.class);
//                intent.putExtra("menu", "now_playing");
//                intent.putExtra("titulo", "Nos Cinemas");
//                intent.putExtra("tipo", 1);
//                startActivity(intent);
//            }
//        });

        return view;
    }

}
