package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class Login extends AppCompatActivity {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private EditText txt_email, txt_senha;
    private Button btn_logar, btn_inscrever;
    private CallbackManager mCallbackManager;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_senha = (EditText) findViewById(R.id.txt_senha);
        btn_logar = (Button) findViewById(R.id.btn_logar);
        //btn_facebook = (Button) findViewById(R.id.login_button_facebook);
        btn_inscrever = (Button) findViewById(R.id.btn_inscrever);
//        try{ logger.debug("Checking signs");
//            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                logger.debug(Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            logger.debug(e.getMessage());
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            logger.debug(e.getMessage());
//        }
//        FacebookSdk.sdkInitialize(getApplicationContext());

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.tcc.zeugmaproject.zeugmaproject",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        loginButton.setReadPermissions("public_profile", "user_friends", "email");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("aqui", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("aqui", "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("aqui", "facebook:onError", error);
                        // ...
                    }
                });
        btn_logar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Logar();
            }
        });

        btn_inscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registrar.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void handleFacebookAccessToken(AccessToken token){
        if( token != null ){
            Log.i("aqui meu", "token facebook:" + token);

            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser user = firebaseAuth.getCurrentUser();
                                cadastraUsuarioFacebook(user);
                                Log.i("aqui meu", "criando intent");
                                Intent i = new Intent(Login.this, MainController.class);
                                startActivity(i);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i("aqui meu", "signInWithCredential:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Falha ao logar.",  Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }else{
            firebaseAuth.signOut();
        }
    }

    private void cadastraUsuarioFacebook(final FirebaseUser user) {
        DatabaseReference firabaseReference = ControleFirebase.getFirebaseReference();
        firabaseReference.child("usuario").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i("aqui meu", dataSnapshot.toString());
                if (dataSnapshot.getValue() == null){
                    DatabaseReference firabaseReference_aux = ControleFirebase.getFirebaseReference();
                    if (user.getEmail() != null)
                        firabaseReference_aux.child("usuario").child(user.getUid()).child("email").setValue(user.getEmail());

                    if (user.getDisplayName() != null)
                        firabaseReference_aux.child("usuario").child(user.getUid()).child("nome").
                                setValue(user.getDisplayName());
                    firabaseReference_aux.child("usuario").child(user.getUid()).child("privacidade").
                            child("assistidos").setValue(0);
                    firabaseReference_aux.child("usuario").child(user.getUid()).child("privacidade").
                            child("favoritos").setValue(0);
                    firabaseReference_aux.child("usuario").child(user.getUid()).child("privacidade").
                            child("curtidas").setValue(0);
                    firabaseReference_aux.child("usuario").child(user.getUid()).child("privacidade").
                            child("descurtidas").setValue(0);
                    firabaseReference_aux.child("usuario").child(user.getUid()).child("privacidade").
                            child("meus amigos").setValue(0);
                    if (user.getPhotoUrl() != null) {
                        cadastraFotoPerfil(user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void cadastraFotoPerfil(final FirebaseUser user) {
        final StorageReference storageRef = ControleFirebase.getStorageReference().child("usuario").child(user.getUid()).
                child("porfile_pic");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(final Uri uri) {
                // Got the download URL for 'users/me/profile.png'
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                new Thread() {
                    public void run() {
                        try {
                            URL url = new URL(user.getPhotoUrl().toString());
                            HttpURLConnection conexao;
                            conexao = (HttpURLConnection) url.openConnection();
                            InputStream input = conexao.getInputStream();
                            final Bitmap btmp = (BitmapFactory.decodeStream(input));
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            btmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = storageRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.i("aqui meu", "sub taks");
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    private void Logar(){
        String email = txt_email.getText().toString();
        String senha = txt_senha.getText().toString();
        if ((email.isEmpty()) || (senha.isEmpty())){
            Toast.makeText(getApplication().getApplicationContext(), "Favor Preencher email e senha", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(Login.this, MainController.class));
                        finish();
                    } else {
                        Toast.makeText(getApplication().getApplicationContext(), "Usuario ou senha invalidos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
            firebaseAuth.getCurrentUser().reload();
        if ((firebaseAuth.getCurrentUser() != null) && ((logadoComFacebook()) || (firebaseAuth.getCurrentUser().isEmailVerified()))){
            startActivity(new Intent(Login.this, MainActivity.class));
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
            firebaseAuth.getCurrentUser().reload();
        if ((firebaseAuth.getCurrentUser() != null) && ((logadoComFacebook()) || (firebaseAuth.getCurrentUser().isEmailVerified()))){
            startActivity(new Intent(Login.this, MainActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null)
            firebaseAuth.getCurrentUser().reload();
        if ((firebaseAuth.getCurrentUser() != null) && ((logadoComFacebook()) || (firebaseAuth.getCurrentUser().isEmailVerified()))){
            startActivity(new Intent(Login.this, MainActivity.class));
        }
    }

    public boolean logadoComFacebook(){
        boolean aux = false;
        for (String s :ControleFirebase.getAuthReference().getCurrentUser().getProviders()){
            if (s.contains("facebook")) {
                aux = true;
            }
        }
        return aux;
    }
}
