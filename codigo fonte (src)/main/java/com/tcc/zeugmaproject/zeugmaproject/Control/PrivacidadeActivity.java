package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ListasAdapter;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.util.ArrayList;
import java.util.List;

public class PrivacidadeActivity extends AppCompatActivity {
    private List<String> listas = new ArrayList<>();
    private List<Integer> posicoes = new ArrayList<>();
    private ListasAdapter listaAdapter;
    private RecyclerView mRecyclerView;
    private DatabaseReference firabaseReference= FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacidade);

        mRecyclerView = (RecyclerView) findViewById(R.id.privacidade_recycler_view);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(LayoutManager);
        listas = new ArrayList<>();
        atualizaListas();
    }

    public void atualizaListas(){
        firabaseReference.child("usuario").child(ControleFirebase.getUserID()).child("privacidade").
            addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listas = new ArrayList<>();
                    posicoes = new ArrayList<>();
                    listaAdapter = new ListasAdapter(listas, posicoes, PrivacidadeActivity.this, true);
                    mRecyclerView.setAdapter(listaAdapter);
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        listas.add(postSnapshot.getKey());
                        posicoes.add(((Long) postSnapshot.getValue()).intValue());

                    }
                    listaAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

}
