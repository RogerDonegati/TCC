package com.tcc.zeugmaproject.zeugmaproject.Model.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tcc.zeugmaproject.zeugmaproject.Control.ControleFirebase;
import com.tcc.zeugmaproject.zeugmaproject.Control.MostraListasActivity;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListasAdapter extends RecyclerView.Adapter<ListasAdapter.MyViewHolder> {
    private List<String> lista = new ArrayList<>();
    private List<Integer> posicoes = new ArrayList<>();
    private final CharSequence[] items = {"Publica", "Somente Amigos", "Particular"};
    private Context contexto;
    private AlertDialog alerta;
    private DatabaseReference firabaseReferenceUsuario = FirebaseDatabase.getInstance().getReference();
    private boolean controla_privacidade;

    public ListasAdapter(List<String> lista, List<Integer> posicoes, Context ctx, boolean controla_privacidade) {
        this.contexto = ctx;
        this.lista = lista;
        this.posicoes = posicoes;
        this.controla_privacidade = controla_privacidade;
        firabaseReferenceUsuario  = ControleFirebase.getFirebaseRefenceUsuario();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_txt, null);
        return new MyViewHolder(itemView);
    }
    public View getView(){
        View itemView = LayoutInflater.from(contexto).inflate(R.layout.adapter_txt, null);
        return itemView;
    }
    @Override
    public void onBindViewHolder(final ListasAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(lista.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (controla_privacidade) {
                         AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                        try {
                            builder.setTitle("Selecionar Privacidade").setSingleChoiceItems(items, posicoes.get(holder.getAdapterPosition()),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int item) {
                                            Map<String, Object> update = new HashMap<String, Object>();
                                            update.put(lista.get(holder.getAdapterPosition()), item);
                                            firabaseReferenceUsuario.child("privacidade").updateChildren(update);
                                            posicoes.set(posicoes.indexOf(posicoes.get(holder.getAdapterPosition())), holder.getAdapterPosition());
                                            alerta.dismiss();
                                        }
                                    });
                            builder.create();
                        } catch (Exception e) {
                            Log.i("Aqui meu ", "erro: " + e.getMessage() + " " + e);
                        }
                        alerta = builder.create();
                        alerta.show();
                    }else {
                        Intent intent = new Intent(contexto, MostraListasActivity.class);
                        intent.putExtra("lista", lista.get(holder.getAdapterPosition()));
                        intent.putExtra("titulo", lista.get(holder.getAdapterPosition()));
                        contexto.startActivity(intent);
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.txt_adapter);
        }
    }
}
