package com.tcc.zeugmaproject.zeugmaproject.Model.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tcc.zeugmaproject.zeugmaproject.Control.MovieDetalhado;
import com.tcc.zeugmaproject.zeugmaproject.Control.SerieDetalhado;
import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;
import com.tcc.zeugmaproject.zeugmaproject.Model.Movie;
import com.tcc.zeugmaproject.zeugmaproject.Model.Serie;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class ConteudoAdapter extends RecyclerView.Adapter<ConteudoAdapter.MyViewHolder> {
    private List<Conteudo> conteudo = new ArrayList<>();
    Context contexto;

    public ConteudoAdapter(List<Conteudo> conteudos, Context ctx) {
        this.contexto = ctx;
        this.conteudo = conteudos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conteudo, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ConteudoAdapter.MyViewHolder holder,  int position) {
        holder.imageView.setAdjustViewBounds(true);
        holder.imageView.setImageBitmap(conteudo.get(position).getBitmap());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (conteudo.get(holder.getAdapterPosition()).getTipo() == 1) {
                    Intent intent = new Intent(contexto, MovieDetalhado.class);
                    intent.putExtra("id", conteudo.get(holder.getAdapterPosition()).getId());

                    Bitmap bmp = conteudo.get(holder.getAdapterPosition()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("poster", byteArray);

                    contexto.startActivity(intent);
                } else {
                    Intent intent = new Intent(contexto, SerieDetalhado.class);
                    intent.putExtra("id", conteudo.get(holder.getAdapterPosition()).getId());

                    Bitmap bmp = conteudo.get(holder.getAdapterPosition()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("poster", byteArray);

                    contexto.startActivity(intent);
                }
            }

        });

    }

    @Override
    public int getItemCount() {
        return conteudo.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
        }
    }
}
