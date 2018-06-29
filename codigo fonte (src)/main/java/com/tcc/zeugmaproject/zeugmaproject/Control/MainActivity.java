package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tcc.zeugmaproject.zeugmaproject.Control.util.NonSlidingViewPage;
import com.tcc.zeugmaproject.zeugmaproject.Control.util.SlidingTabLayout;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.TabsAdapter;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    //FIREBASE
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference firabaseReference = FirebaseDatabase.getInstance().getReference();

    static HttpResponseCache cache;

    //INTERFACE GRAFICA
    private SlidingTabLayout slidingTabLayout;
    private NonSlidingViewPage viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ativaCache();
        intitView();
        ImageView img = (ImageView) findViewById(R.id.btn_logout);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, MainController.class));
            }
        });

//        Log.i("Aqui meu ", "tamanho cache: " + String.valueOf(cache.getHitCount()));
//        Log.e("Aqui meu", "tamanho cache: " + String.valueOf(cache.getNetworkCount()));


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
//        cache.flush();
//        try {
//            cache.close();
//        }catch (Exception e ){
//            Log.i("Aqui meu", "erro ao fechar cache: " + e.getMessage());
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, Login.class));
        }
    }

    public void esconderTeclado(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void copiar(Context context, String text) {
        //Função para copiar, pique boleto bancario
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Texto copiado: ", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "Texto copiado", Toast.LENGTH_LONG).show();
    }

    //Inicializa interface grafica e cria suas referencias
    private void intitView() {

        //Configura abas
        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_main3);
        viewPager = (NonSlidingViewPage) findViewById(R.id.view_pager_main3);
        slidingTabLayout.setCustomTabView(R.layout.adapter_txt, R.id.txt_adapter);
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter( tabsAdapter );
        viewPager.setOffscreenPageLimit(2);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
    }

    private void ativaCache() {
        try {
            File httpCacheDir = new File(getApplicationContext().getCacheDir() , "http");
            long httpCacheSize = 30 * 1024 * 1024; // 10 MiB
            cache = HttpResponseCache.install(httpCacheDir, httpCacheSize);
            Log.i("Aqui meu " , "Cache inciada");
            Toast.makeText(getApplicationContext(), "Cache Ativada", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Log.i("Aqui meu" , "Erro na cache OVER ICS: HTTP response cache failed:" + e);
        }

    }

}

