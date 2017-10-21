package com.tcc.zeugmaproject.zeugmaproject.Control;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.tcc.zeugmaproject.zeugmaproject.Control.TMDBServices.RetrofitAsyncTasks;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ImagemAdapatdor;
import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class ListaConteudo extends AppCompatActivity {
    private List<Conteudo> conteudoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_conteudo);
        GridView gridView = (GridView) findViewById(R.id.grdViewLista);
        conteudoList = new ArrayList<>();


        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            TextView titulo = (TextView) findViewById(R.id.txt_lista_titulo);
            titulo.setText(extra.getString("titulo"));
            ImagemAdapatdor adapter = new ImagemAdapatdor(conteudoList, getApplication().getApplicationContext());
            gridView.setAdapter(adapter);
            RetrofitAsyncTasks asyncTask1 = new RetrofitAsyncTasks(1, extra.getString("menu"),  extra.getInt("tipo"), 25, null, adapter, conteudoList);
            asyncTask1.executaParalelo(asyncTask1);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent;
                if (conteudoList.get(position).getTipo() == 1)
                    intent = new Intent(getApplicationContext(), MovieDetalhado.class);
                else
                    intent = new Intent(getApplicationContext(), SerieDetalhado.class);

                intent.putExtra("id", conteudoList.get(position).getId());
                Bitmap bmp = conteudoList.get(position).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("poster", byteArray);
                startActivity(intent);

            }
        });
    }
}
