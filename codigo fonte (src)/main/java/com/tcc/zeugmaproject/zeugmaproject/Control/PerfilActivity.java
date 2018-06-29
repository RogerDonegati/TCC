package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ListasAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Usuario;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PerfilActivity extends AppCompatActivity {

    private int curtidas =0, descurtidas =0, favoritos=0,assistidos =0, tempo_total =0, tempo_filmes =0, tempo_series =0;
    private Handler handler = new Handler();
    private ImageView img;
    private List<String> listas = new ArrayList<>();
    private List<Integer> posicoes = new ArrayList<>();
    private ListasAdapter listaAdapter;
    private int privacidade;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        DatabaseReference firabaseReference = ControleFirebase.getFirebaseReference();
        firabaseReference.child("usuario").child(ControleFirebase.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            exibePerfil(dataSnapshot.getValue(Usuario.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StorageReference storageRef;
        storageRef = ControleFirebase.getStorageReference();
        storageRef = storageRef.child("usuario").child(ControleFirebase.getUserID()).child("porfile_pic");
        if( requestCode==1 && resultCode == RESULT_OK && data != null ) {
            UploadTask uploadTask;
            Uri localImagemSelecionada = data.getData();
            uploadTask = storageRef.putFile(localImagemSelecionada);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Erro ao postar sua imagem - Tente novamente!", Toast.LENGTH_LONG ).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    img.setImageURI(data.getData());
                }
            });
        }
    }

    public void exibePerfil(Usuario usuario){

        DatabaseReference firabaseReference = ControleFirebase.getFirebaseReference();
        TextView txt_nome = (TextView) findViewById(R.id.txt_perfil_nome);
        img = (ImageView) findViewById(R.id.img_perfil);
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_perfil);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(LayoutManager);



        txt_nome.setText(usuario.getNome());

        carregaImagem();
        carregaAssistidos();
        carregaFavoritos();
        carregaCurtidas();
        carregaDescurtidas();
        carregaImagem();

        if (ControleFirebase.getUserID().equals(ControleFirebase.getUserID())){
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
            });
        }

        firabaseReference.child("usuario").child(ControleFirebase.getUserID()).child("privacidade").
            addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listas = new ArrayList<>();
                    posicoes = new ArrayList<>();
                    listaAdapter = new ListasAdapter(listas, posicoes, PerfilActivity.this, false);
                    mRecyclerView.setAdapter(listaAdapter);
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if ((ControleFirebase.getFirebaseRefenceUsuario().getKey().equals(ControleFirebase.getUserID()))
                            || (privacidade >= ((Long) postSnapshot.getValue()).intValue())){
                            listas.add(postSnapshot.getKey());
                            posicoes.add(((Long) postSnapshot.getValue()).intValue());
                        }
                    }
                    listaAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(mRecyclerView);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



    }

    public void carregaImagem() {
        StorageReference storageRef;
        storageRef = ControleFirebase.getStorageReference();
        storageRef = storageRef.child("usuario").child(ControleFirebase.getUserID()).child("porfile_pic");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                new Thread() {
                    public void run() {
                        try {
                            URL url = new URL(uri.toString());
                            HttpURLConnection conexao;
                            conexao = (HttpURLConnection) url.openConnection();
                            InputStream input = conexao.getInputStream();
                            final Bitmap btmp = (BitmapFactory.decodeStream(input));
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    img.setImageBitmap(btmp);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    public void carregaAssistidos(){
        DatabaseReference firabaseReference = ControleFirebase.getFirebaseReference();
        final TextView txt_assistidos = (TextView) findViewById(R.id.txt_perfil_asistidos);
        final TextView txt_tempo_total = (TextView) findViewById(R.id.txt_perfil_tempo_total);
        final TextView txt_tempo_filme = (TextView) findViewById(R.id.txt_perfil_tempo_filme);
        final TextView txt_tempo_serie = (TextView) findViewById(R.id.txt_perfil_tempo_serie);
        firabaseReference.child("assistidos").child("filme").orderByChild(ControleFirebase.getUserID()).equalTo(true).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        assistidos = assistidos + ((int) dataSnapshot.getChildrenCount());
                        txt_assistidos.setText("Assistidos: " + Integer.toString(assistidos));
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            tempo_filmes = tempo_filmes + postSnapshot.child("runtime").getValue(int.class);
                            txt_tempo_filme.setText("Tempo Gasto com filmes: " + tempo_filmes);
                        }
                        tempo_total = tempo_total + tempo_filmes;
                        txt_tempo_total.setText("Tempo total gasto: " + tempo_total);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        firabaseReference.child("assistidos").child("serie").orderByChild(ControleFirebase.getUserID()).equalTo(true).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        assistidos = assistidos + ((int) dataSnapshot.getChildrenCount());
                        txt_assistidos.setText("Assistidos :" + Integer.toString(assistidos));
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            tempo_series = tempo_series + postSnapshot.child("runtime").getValue(int.class);
                            txt_tempo_serie.setText("Tempo Gasto com series: " + tempo_series);
                        }
                        tempo_total = tempo_total + tempo_series;
                        txt_tempo_total.setText("Tempo total gasto: " + tempo_total);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void carregaFavoritos(){
        DatabaseReference firabaseReference = ControleFirebase.getFirebaseReference();
        final TextView txt_favoritos = (TextView) findViewById(R.id.txt_perfil_favoritos);
        firabaseReference.child("favoritos").child("filme").orderByChild(ControleFirebase.getUserID()).equalTo(true).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        favoritos = favoritos + ((int) dataSnapshot.getChildrenCount());
                        txt_favoritos.setText("Favoritos: " + Integer.toString(favoritos));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        firabaseReference.child("favoritos").child("serie").orderByChild(ControleFirebase.getUserID()).equalTo(true).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        favoritos = favoritos + ((int) dataSnapshot.getChildrenCount());
                        txt_favoritos.setText("Favoritos: " + Integer.toString(favoritos));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void carregaCurtidas(){
        DatabaseReference firabaseReference = ControleFirebase.getFirebaseReference();
        final TextView txt_curtidas = (TextView) findViewById(R.id.txt_perfil_curtidas);
        firabaseReference.child("curtidas").child("filme").orderByChild(ControleFirebase.getUserID()).equalTo(true).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        curtidas = curtidas + ((int) dataSnapshot.getChildrenCount());
                        txt_curtidas.setText("Curtidas: " + Integer.toString(curtidas));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        firabaseReference.child("curtidas").child("serie").orderByChild(ControleFirebase.getUserID()).equalTo(true).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        curtidas = curtidas + ((int) dataSnapshot.getChildrenCount());
                        txt_curtidas.setText("Curtidas: " + Integer.toString(curtidas));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void carregaDescurtidas(){
        DatabaseReference firabaseReference = ControleFirebase.getFirebaseReference();
        final TextView txt_descurtidas = (TextView) findViewById(R.id.txt_perfil_descurtidas);
        firabaseReference.child("descurtidas").child("filme").orderByChild(ControleFirebase.getUserID()).equalTo(true).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        descurtidas = descurtidas + ((int) dataSnapshot.getChildrenCount());
                        txt_descurtidas.setText("Descurtidas: " + Integer.toString(descurtidas));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        firabaseReference.child("descurtidas").child("serie").orderByChild(ControleFirebase.getUserID()).equalTo(true).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        descurtidas = descurtidas + ((int) dataSnapshot.getChildrenCount());
                        txt_descurtidas.setText("Descurtidas: " + Integer.toString(descurtidas));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void setListViewHeightBasedOnChildren(RecyclerView listView) {
        if (listaAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listaAdapter.getItemCount(); i++) {
            view = listaAdapter.getView();
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getMeasuredHeight() * (listaAdapter.getItemCount() - 1));
        listView.setLayoutParams(params);
    }

}

