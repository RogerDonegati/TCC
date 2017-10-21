package com.tcc.zeugmaproject.zeugmaproject.Control;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public final class ControleFirebase {

    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference firabaseReference;
    private static DatabaseReference firabaseReferenceUsuario;
    private static StorageReference storageReference;

    public static String getUserID(){
        if (firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth.getCurrentUser().getUid();
    }

    public static FirebaseAuth getAuthReference(){
        if (firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    public static DatabaseReference getFirebaseReference(){
        if (firabaseReference == null){
            firabaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return firabaseReference;
    }

    public static DatabaseReference getFirebaseRefenceUsuario (){
        if (firabaseReferenceUsuario == null){
            firabaseReferenceUsuario = FirebaseDatabase.getInstance().getReference();
            firabaseReferenceUsuario = firabaseReferenceUsuario.child("usuario").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        return firabaseReferenceUsuario;
    }

    public static StorageReference getStorageReference(){
        if (storageReference == null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
        }
        return storageReference;
    }
}