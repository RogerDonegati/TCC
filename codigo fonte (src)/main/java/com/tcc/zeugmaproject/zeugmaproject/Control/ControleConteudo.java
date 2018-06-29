package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;

import java.util.HashMap;
import java.util.Map;

public class ControleConteudo {
    private DatabaseReference firabaseReferenceListas;

    public ControleConteudo() {
        firabaseReferenceListas = ControleFirebase.getFirebaseReference();
    }

    public void adicionaFavorito(Conteudo c){
        try {
            if (c.getTipo() == 1) {
                firabaseReferenceListas.child("favoritos").child("filme").child(c.getId()).
                        child(ControleFirebase.getUserID()).setValue(true);
                firabaseReferenceListas.child("favoritos").child("filme").child(c.getId()).
                        child("poster_path").setValue(c.getPoster_path());
            }else {
                firabaseReferenceListas.child("favoritos").child("serie").child(c.getId()).
                        child(ControleFirebase.getUserID()).setValue(true);
                firabaseReferenceListas.child("favoritos").child("serie").child(c.getId()).
                        child("poster_path").setValue(c.getPoster_path());
            }
        }catch (Exception e ){
            Log.i("aqui meu", e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeFavorito(Conteudo c){
        if (c.getTipo() == 1)
          firabaseReferenceListas.child("favoritos").child("filme").child(c.getId()).
                  child(ControleFirebase.getUserID()).setValue(null);
        else
            firabaseReferenceListas.child("favoritos").child("serie").child(c.getId()).
                    child(ControleFirebase.getUserID()).setValue(null);

    }

    public void adicionaAssistido(Conteudo c){
        if (c.getTipo() == 1) {
            Map<String, Object> userUpdates = new HashMap<String, Object>();
            userUpdates.put(c.getId() + "/runtime", c.getRuntime());
            userUpdates.put(c.getId() + "/poster_path", c.getPoster_path());
            userUpdates.put(c.getId() + "/poster_path", c.getPoster_path());
            firabaseReferenceListas.child("assistidos").child("filme").updateChildren(userUpdates);
            firabaseReferenceListas.child("assistidos").child("filme").child(c.getId()).
                    child(ControleFirebase.getUserID()).setValue(true);
        }else{
            Map<String, Object> userUpdates = new HashMap<String, Object>();
            userUpdates.put(c.getId() + "/runtime", c.getRuntime());
            userUpdates.put(c.getId() + "/poster_path", c.getPoster_path());
            firabaseReferenceListas.child("assistidos").child("serie").updateChildren(userUpdates);
            firabaseReferenceListas.child("assistidos").child("serie").child(c.getId()).
                    child(ControleFirebase.getUserID()).setValue(true);
        }
    }

    public void removeAssitido(Conteudo c){
        if (c.getTipo() == 1 )
          firabaseReferenceListas.child("assistidos").child("filme").child(c.getId()).
                    child(ControleFirebase.getUserID()).setValue(null);
        else
            firabaseReferenceListas.child("assistidos").child("serie").child(c.getId()).
                    child(ControleFirebase.getUserID()).setValue(null);

    }

    public void curtir(final Conteudo c){
        if (c.getTipo() == 1){
            firabaseReferenceListas.child("filme").child(c.getId()).child("poster_path").setValue(c.getPoster_path());
            firabaseReferenceListas.child("curtidas").child("filme").child(c.getId()).child("poster_path").setValue(c.getPoster_path());
            firabaseReferenceListas.child("curtidas").child("filme").child(c.getId()).child(ControleFirebase.getUserID()).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                        firabaseReferenceListas.child("curtidas").child("filme").child(c.getId()).child(ControleFirebase.getUserID()).
                                setValue(null);
                        atualizaCurtidaPalhativamente(c);
                    }else{
                        firabaseReferenceListas.child("curtidas").child("filme").child(c.getId()).child(ControleFirebase.getUserID()).
                                setValue(true);
                        atualizaCurtidaPalhativamente(c);
//REMOVE DESCURTIDA CASO CURTA
                        firabaseReferenceListas.child("descurtidas").child("filme").child(c.getId()).child(ControleFirebase.getUserID()).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               if (dataSnapshot.getValue() != null) {
                                   firabaseReferenceListas.child("descurtidas").child("filme").child(c.getId()).child(ControleFirebase.getUserID()).
                                           setValue(null);
                                   atualizaDescurtidaPalhativamente(c);
                               }
                           }
                           @Override
                           public void onCancelled(DatabaseError databaseError) {
                           }
                           }
                        );
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }else{
            firabaseReferenceListas.child("serie").child(c.getId()).child("poster_path").setValue(c.getPoster_path());
            firabaseReferenceListas.child("curtidas").child("serie").child(c.getId()).child("poster_path").setValue(c.getPoster_path());
            firabaseReferenceListas.child("curtidas").child("serie").child(c.getId()).child(ControleFirebase.getUserID()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       if (dataSnapshot.getValue() != null) {
                           firabaseReferenceListas.child("curtidas").child("serie").child(c.getId()).child(ControleFirebase.getUserID()).
                                   setValue(null);
                           atualizaCurtidaPalhativamente(c);
                       }else{
                           firabaseReferenceListas.child("curtidas").child("serie").child(c.getId()).child(ControleFirebase.getUserID()).
                                   setValue(true);
                           atualizaCurtidaPalhativamente(c);
//REMOVE DESCURTIDA CASO CURTA
                           firabaseReferenceListas.child("descurtidas").child("serie").child(c.getId()).child(ControleFirebase.getUserID()).
                               addListenerForSingleValueEvent(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(DataSnapshot dataSnapshot) {
                                      try {
                                          if (dataSnapshot.getValue() != null) {
                                              firabaseReferenceListas.child("descurtidas").child("serie").child(c.getId()).
                                                      child(ControleFirebase.getUserID()).setValue(null);
                                              atualizaDescurtidaPalhativamente(c);
                                          }
                                      }catch (Exception e ){
                                          Log.i("aqui meu", "erro remover curtir");
                                      }
                                  }
                                  @Override
                                  public void onCancelled(DatabaseError databaseError) {
                                  }
                               });
                       }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
                });
        }
    }

    public void descurtir(final Conteudo c){
        if (c.getTipo() == 1){
            firabaseReferenceListas.child("filme").child(c.getId()).child("poster_path").setValue(c.getPoster_path());
            firabaseReferenceListas.child("descurtidas").child("filme").child(c.getId()).child("poster_path").setValue(c.getPoster_path());
            firabaseReferenceListas.child("descurtidas").child("filme").child(c.getId()).child(ControleFirebase.getUserID()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null){
                            firabaseReferenceListas.child("descurtidas").child("filme").child(c.getId()).child(ControleFirebase.getUserID()).
                                    setValue(null);
                            atualizaDescurtidaPalhativamente(c);
                        }else{
                            firabaseReferenceListas.child("descurtidas").child("filme").child(c.getId()).child(ControleFirebase.getUserID()).
                                    setValue(true);
                            atualizaDescurtidaPalhativamente(c);
//REMOVE DESCURTIDA CASO CURTA
                            firabaseReferenceListas.child("curtidas").child("filme").child(c.getId()).child(ControleFirebase.getUserID()).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       try {
                                           if (dataSnapshot.getValue() != null) {
                                               firabaseReferenceListas.child("curtidas").child("filme").child(c.getId()).
                                                       child(ControleFirebase.getUserID()).setValue(null);
                                               atualizaCurtidaPalhativamente(c);
                                           }
                                       }catch (Exception e ){
                                           Log.i("aqui meu", "erro remover curtir");
                                       }
                                   }
                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {
                                   }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }else{
            firabaseReferenceListas.child("serie").child(c.getId()).child("poster_path").setValue(c.getPoster_path());
            firabaseReferenceListas.child("descurtidas").child("serie").child(c.getId()).child("poster_path").setValue(c.getPoster_path());
            firabaseReferenceListas.child("descurtidas").child("serie").child(c.getId()).child(ControleFirebase.getUserID()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            firabaseReferenceListas.child("descurtidas").child("serie").child(c.getId()).
                                    child(ControleFirebase.getUserID()).setValue(null);
                            atualizaDescurtidaPalhativamente(c);
                        }else{
                            firabaseReferenceListas.child("descurtidas").child("serie").child(c.getId()).
                                    child(ControleFirebase.getUserID()).setValue(true);
                            atualizaDescurtidaPalhativamente(c);
//REMOVE DESCURTIDA CASO CURTA
                            firabaseReferenceListas.child("curtidas").child("serie").child(c.getId()).child(ControleFirebase.getUserID()).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.getValue() != null) {
                                                firabaseReferenceListas.child("curtidas").child("serie").child(c.getId()).
                                                        child(ControleFirebase.getUserID()).setValue(null);
                                                atualizaCurtidaPalhativamente(c);
                                            }
                                        }catch (Exception e ){
                                            Log.i("aqui meu", "erro remover curtir");
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void atualizaDescurtidaPalhativamente(final Conteudo c){
        DatabaseReference firabaseReference_aux;
        if (c.getTipo() == 1)
            firabaseReference_aux = firabaseReferenceListas.child("descurtidas").child("filme").child(c.getId());
        else
            firabaseReference_aux = firabaseReferenceListas.child("descurtidas").child("serie").child(c.getId());

        firabaseReference_aux.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (c.getTipo() == 1)
                    firabaseReferenceListas.child("filme").child(c.getId()).child("descurtidas").setValue(dataSnapshot.getChildrenCount()-1);
                else
                    firabaseReferenceListas.child("serie").child(c.getId()).child("descurtidas").setValue(dataSnapshot.getChildrenCount()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void atualizaCurtidaPalhativamente(final Conteudo c){
        DatabaseReference firabaseReference_aux;
        if (c.getTipo() == 1)
            firabaseReference_aux = firabaseReferenceListas.child("curtidas").child("filme").child(c.getId());
        else
            firabaseReference_aux = firabaseReferenceListas.child("curtidas").child("serie").child(c.getId());

        firabaseReference_aux.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (c.getTipo()==1)
                    firabaseReferenceListas.child("filme").child(c.getId()).child("curtidas").setValue(dataSnapshot.getChildrenCount() -1);
                else
                    firabaseReferenceListas.child("serie").child(c.getId()).child("curtidas").setValue(dataSnapshot.getChildrenCount()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void adicionaCurtida(Conteudo c) {
        DatabaseReference firabaseReference_aux;
        if (c.getTipo() == 1)
            firabaseReference_aux = firabaseReferenceListas.child("filme").child(c.getId()).child("curtidas");
        else
            firabaseReference_aux = firabaseReferenceListas.child("serie").child(c.getId()).child("curtidas");

        firabaseReference_aux.runTransaction(new Transaction.Handler(){
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Log.d("aqui meu", "transação realizada:" + mutableData.toString());
                Integer curtidas = mutableData.getValue(Integer.class);
                if (curtidas == null)
                    mutableData.setValue(1);
                else
                    mutableData.setValue(curtidas + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("aqui meu", "transação realizada:" + databaseError);
            }
        },true);
    }

    private void removeCurtida(Conteudo c) {
        DatabaseReference firabaseReference_aux;
        if (c.getTipo() == 1)
            firabaseReference_aux = firabaseReferenceListas.child("filme").child(c.getId()).child("curtidas");
        else
            firabaseReference_aux = firabaseReferenceListas.child("serie").child(c.getId()).child("curtidas");

        Log.d("aqui meu", "remove:" + firabaseReference_aux.toString());
        firabaseReference_aux.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Log.d("aqui meu", "transação realizada:" + mutableData.toString());
                Integer curtidas = mutableData.getValue(Integer.class);
                if (curtidas == null)
                    mutableData.setValue(0);
                else
                    mutableData.setValue(curtidas - 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("aqui meu", "transação realizada:" + databaseError);
            }
        });
    }

    private void adicionaDescurtida(Conteudo c) {
        DatabaseReference firabaseReference_aux;
        if (c.getTipo() == 1)
            firabaseReference_aux = firabaseReferenceListas.child("filme").child(c.getId()).child("descurtidas");
        else
            firabaseReference_aux = firabaseReferenceListas.child("serie").child(c.getId()).child("descurtidas");

        firabaseReference_aux.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer curtidas = mutableData.getValue(Integer.class);
                if (curtidas == null)
                    mutableData.setValue(1);
                else
                    mutableData.setValue(curtidas + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("aqui meu", "transação realizada:" + databaseError);
            }
        });
    }

    private void removeDescurtida(Conteudo c) {
        DatabaseReference firabaseReference_aux;
        if (c.getTipo() == 1)
            firabaseReference_aux = firabaseReferenceListas.child("filme").child(c.getId()).child("descurtidas");
        else
            firabaseReference_aux = firabaseReferenceListas.child("serie").child(c.getId()).child("descurtidas");

        firabaseReference_aux.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer curtidas = mutableData.getValue(Integer.class);
                if (curtidas == null)
                    mutableData.setValue(0);
                else
                    mutableData.setValue(curtidas - 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("aqui meu", "transação realizada:" + databaseError);
            }
        });
    }
}