package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tcc.zeugmaproject.zeugmaproject.Control.util.NonSlidingViewPage;
import com.tcc.zeugmaproject.zeugmaproject.Control.util.SlidingTabLayout;
import com.tcc.zeugmaproject.zeugmaproject.Control.util.ExpandListAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.TabsAdapter;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //FIREBASE
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference firabaseReference = FirebaseDatabase.getInstance().getReference();

    static HttpResponseCache cache;


    //SLIDING PAGE
    private SlidingTabLayout slidingTabLayout;
    private NonSlidingViewPage viewPager;

    //DRAWER MENU
    private HashMap<String, List<String>> listDataChild;
    private List<String> listDataHeader;
    private ExpandableListView expListView;
    private ExpandListAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        ativaCache();
        intitView();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //icone msgs padrao
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
            firebaseAuth.getCurrentUser().reload();
        if (firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(MainController.this, Login.class));
        }else if ((!logadoComFacebook()) && (!firebaseAuth.getCurrentUser().isEmailVerified())){
            firebaseAuth.getCurrentUser().sendEmailVerification();
            Toast.makeText(getApplication().getApplicationContext(), "Favor confirmar seu email", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainController.this, Login.class));
        }
    }

    private void intitView() {
        //Configura abas
        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager(), this);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tab_main);
        viewPager = (NonSlidingViewPage) findViewById(R.id.view_pager_main);
        slidingTabLayout.setCustomTabView(R.layout.adapter_view_pager, R.id.txt_view_pager);
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter( tabsAdapter );
      //  viewPager.setOffscreenPageLimit(2);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
        enableExpandableList();
    }

    private void ativaCache() {
        try {
            File httpCacheDir = new File(getApplicationContext().getCacheDir() , "http");
            long httpCacheSize = 30 * 1024 * 1024; // 10 MiB
            cache = HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i("Aqui meu" , "Erro na cache OVER ICS: HTTP response cache failed:" + e);
        }
    }
    @Override

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void enableExpandableList() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        expListView = (ExpandableListView) findViewById(R.id.left_drawer);

        prepareListData(listDataHeader, listDataChild);
        listAdapter = new ExpandListAdapter(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
          //      Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition) + " Expanded", Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
       //         Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " Collapsed", Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // Temporary code:

                // till here
                if (groupPosition == 0){
                    if (childPosition == 0 ){

                    }else if (childPosition == 1 ){
                        startActivity(new Intent(MainController.this, PerfilActivity.class));
                    }
                }else if (groupPosition == 1 ){
                    if (childPosition == 0 ){
                        Intent intent = new Intent(getApplicationContext(), MostraListasActivity.class);
                        intent.putExtra("lista", "favoritos");
                        intent.putExtra("titulo", "Favoritos");
                        startActivity(intent);
                    }else if (childPosition == 1){
                        Intent intent = new Intent(getApplicationContext(), MostraListasActivity.class);
                        intent.putExtra("lista", "assistidos");
                        intent.putExtra("titulo", "Assistido");
                        startActivity(intent);
                    }else if (childPosition == 2){
                        Intent intent = new Intent(getApplicationContext(), MostraListasActivity.class);
                        intent.putExtra("lista", "curtidas");
                        intent.putExtra("titulo", "Curtidas");
                        startActivity(intent);
                    }else if (childPosition == 3){
                        Intent intent = new Intent(getApplicationContext(), MostraListasActivity.class);
                        intent.putExtra("lista", "descurtidas");
                        intent.putExtra("titulo", "Descurtidas");
                        startActivity(intent);
                    } else if (childPosition == 4){
                        startActivity(new Intent(MainController.this, ConfiguracaoActivity.class));
                    } else if (childPosition == 5) {
                        startActivity(new Intent(MainController.this, PrivacidadeActivity.class));
                    } else if (childPosition == 6) {
                       firebaseAuth.signOut();
                        startActivity(new Intent(MainController.this, Login.class));
                        finish();
                    }
                } else if (groupPosition == 2){
                    if (childPosition == 0) {
                        Intent intent = new Intent(getApplicationContext(), ListaConteudo.class);
                        intent.putExtra("menu", "popular");
                        intent.putExtra("titulo", "Populares");
                        intent.putExtra("tipo", 1);
                        startActivity(intent);
                    } else if (childPosition == 1){
                        Intent intent = new Intent(getApplicationContext(), ListaConteudo.class);
                        intent.putExtra("menu", "top_rated");
                        intent.putExtra("titulo", "Melhor Avaliados");
                        intent.putExtra("tipo", 1);
                        startActivity(intent);
                    } else if (childPosition == 2){
                        Intent intent = new Intent(getApplicationContext(), ListaConteudo.class);
                        intent.putExtra("menu", "upcoming");
                        intent.putExtra("titulo", "Lançamentos");
                        intent.putExtra("tipo", 1);
                        startActivity(intent);
                    } else if (childPosition == 3){
                        Intent intent = new Intent(getApplicationContext(), ListaConteudo.class);
                        intent.putExtra("menu", "now_playing");
                        intent.putExtra("titulo", "Nos Cinemas");
                        intent.putExtra("tipo", 1);
                        startActivity(intent);
                    }
                }else if (groupPosition == 3){
                    if (childPosition == 0) {
                        Intent intent = new Intent(getApplicationContext(), ListaConteudo.class);
                        intent.putExtra("menu", "popular");
                        intent.putExtra("titulo", "Series Populares");
                        intent.putExtra("tipo", 2);
                        startActivity(intent);
                    }else if (childPosition == 1){
                        Intent intent = new Intent(getApplicationContext(), ListaConteudo.class);
                        intent.putExtra("menu", "top_rated");
                        intent.putExtra("titulo", "Melhor Avaliadas");
                        intent.putExtra("tipo", 2);
                        startActivity(intent);
                        }
                }
//                Toast.makeText( getApplicationContext(), listDataHeader.get(groupPosition) + " : " + listDataChild.get(
//                                listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT)
//                        .show();
                return false;
            }
        });}

    private void prepareListData(List<String> listDataHeader, Map<String, List<String>> listDataChild) {

        // Adding child data
        listDataHeader.add("Social");
        listDataHeader.add("Minha Conta");
        listDataHeader.add("Filmes");
        listDataHeader.add("Series");

        // Adding child data
        List<String> social = new ArrayList<String>();
        social.add("Buscar Amigos");
        social.add("Meu Perfil");

        // Adding child data
        List<String> top = new ArrayList<String>();
        top.add("Favoritos");
        top.add("Assitidos");
        top.add("Curtidas");
        top.add("Descurtidas");
        top.add("Meus Dados");
        top.add("Privacidade");
        top.add("Logout");


        List<String> mid = new ArrayList<String>();
        mid.add("Populares");
        mid.add("Melhor Avaliados");
        mid.add("Lançamentos");
        mid.add("Nos Cinemas");

        List<String> bottom = new ArrayList<String>();
        bottom.add("Populares");
        bottom.add("Melhor Avaliadas");

        listDataChild.put(listDataHeader.get(0), social); // Header, Child data
        listDataChild.put(listDataHeader.get(1), top); // Header, Child data
        listDataChild.put(listDataHeader.get(2), mid);
        listDataChild.put(listDataHeader.get(3), bottom);
    }

    public boolean logadoComFacebook(){
        boolean aux = false;
        for (String s :ControleFirebase.getAuthReference().getCurrentUser().getProviders()){
            if (s.contains("facebook")) {
                Log.i("aqui meu", firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                aux = true;
            }
        }
        return aux;
    }
}
