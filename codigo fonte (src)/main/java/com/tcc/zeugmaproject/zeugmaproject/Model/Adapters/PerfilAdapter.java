package com.tcc.zeugmaproject.zeugmaproject.Model.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.MyViewHolder> {
    private List<String> lista = new ArrayList<>();
    private List<Integer> posicoes = new ArrayList<>();
    private final CharSequence[] items = {"Publica", "Somente Amigos", "Particular"};
    private Context contexto;
    private AlertDialog alerta;


    private DatabaseReference firabaseReferenceListas = FirebaseDatabase.getInstance().getReference();

    public PerfilAdapter(List<String> lista, List<Integer> posicoes, Context ctx) {
        this.contexto = ctx;
        this.lista = lista;
        this.posicoes = posicoes;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firabaseReferenceListas = firabaseReferenceListas.child("usuario").child(firebaseAuth.getCurrentUser().getUid()).child("listas");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_txt, null);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PerfilAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(lista.get(position));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                try {
                    builder.setTitle("Selecionar Privacidade").setSingleChoiceItems(items, posicoes.get(holder.getAdapterPosition()),
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int item) {
                            Map<String, Object> update = new HashMap<String, Object>();
                            update.put(lista.get(holder.getAdapterPosition()) + "/privacidade", item);
                            firabaseReferenceListas.updateChildren(update);
                            posicoes.set(posicoes.indexOf(posicoes.get(holder.getAdapterPosition())), holder.getAdapterPosition());
                            alerta.dismiss();
                        }
                    });
                    builder.create();
                }catch (Exception e ){
                    Log.i("Aqui meu " , "erro: " + e.getMessage() + " " + e);
                }
                alerta = builder.create();
                alerta.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        int posicao;
        public MyViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.txt_adapter);
        }
    }
}
