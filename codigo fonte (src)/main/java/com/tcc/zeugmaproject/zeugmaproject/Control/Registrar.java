package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tcc.zeugmaproject.zeugmaproject.Model.Usuario;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Registrar extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Button btn_registrar;
    private EditText txt_email, txt_senha, txt_senha2, txt_nome, txt_apelido;
    private EditText txt_nascimento;
    private DatabaseReference firabaseReference = FirebaseDatabase.getInstance().getReference();
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        btn_registrar = (Button) findViewById(R.id.btn_registrar);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_senha = (EditText) findViewById(R.id.txt_senha);
        txt_nascimento = (EditText) findViewById(R.id.txt_nascimento);
        txt_senha2 = (EditText) findViewById(R.id.txt_senha2);
        txt_nome = (EditText) findViewById(R.id.txt_nome);
        txt_apelido = (EditText) findViewById(R.id.txt_apelido);
        usuario = new Usuario();
      //  MaskedFormatter formatter = new MaskedFormatter("##/##/####");
      //  txt_nascimento.addTextChangedListener(new MaskedWatcher(formatter, txt_nascimento));

        btn_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline() && validaDados()){
                   RegistrarAuth(usuario.getEmail(), usuario.getSenha());
                }
            }
        });
    }
    private boolean validaDados(){
        String email = txt_email.getText().toString();
        String senha = txt_senha.getText().toString();
        String senha2 = txt_senha2.getText().toString();
        String nome = txt_nome.getText().toString();
        String apelido = txt_apelido.getText().toString();
        String nascimento = txt_nascimento.getText().toString();
        if (email.isEmpty() || senha.isEmpty() || senha2.isEmpty() || nome.isEmpty() || nascimento.isEmpty()) {
            Toast.makeText(getApplication().getApplicationContext(), "Favor Preencher todos os campos", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!senha.equals(senha2)) {
            txt_senha.setError("Senhas não coincidem");
            Toast.makeText(this, "Senhas não coincidem", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidEmail(email)) {
            txt_email.setError("Email Invalido");
            Toast.makeText(this, "Email Invalido", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!validaData(nascimento)){
            txt_nascimento.setError("Nascimento invalido");
            Toast.makeText(this, "Nascimento invalido", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            usuario.setEmail(email);
            usuario.setNascimento(nascimento);
            usuario.setNome(nome);
            usuario.setApelido(apelido);
            usuario.setSenha(senha);
            return true;
        }
    }

    private void RegistrarAuth(String email, String senha) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                firabaseReference.child("usuario").child(task.getResult().getUser().getUid()).setValue(usuario);
                firabaseReference.child("usuario").child(firebaseAuth.getCurrentUser().getUid()).child("privacidade").
                        child("assistidos").setValue(0);
                firabaseReference.child("usuario").child(firebaseAuth.getCurrentUser().getUid()).child("privacidade").
                        child("favoritos").setValue(0);
                firabaseReference.child("usuario").child(firebaseAuth.getCurrentUser().getUid()).child("privacidade").
                        child("curtidas").setValue(0);
                firabaseReference.child("usuario").child(firebaseAuth.getCurrentUser().getUid()).child("privacidade").
                        child("descurtidas").setValue(0);
                firabaseReference.child("usuario").child(firebaseAuth.getCurrentUser().getUid()).child("privacidade").
                        child("meus amigos").setValue(0);
                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                    Toast.makeText(getApplication().getApplicationContext(), "Usuario Cadastrado", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Registrar.this, MainController.class);
                    startActivity(i);
                    finish();
                }else{
                    firebaseAuth.getCurrentUser().sendEmailVerification();
                    Toast.makeText(getApplication().getApplicationContext(), "Foi enviado um email de confirmação", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Registrar.this, Login.class);
                    startActivity(i);
                    finish();
                }
            } else {
                try {
                    throw task.getException();
                } catch(FirebaseAuthWeakPasswordException e) {
                    Toast.makeText(getApplication().getApplicationContext(), "Erro: Senha muito fraca" , Toast.LENGTH_SHORT).show();
                    txt_senha.setError("Senha muito fraca");
                    txt_senha.setText("");
                    txt_senha2.setText("");
                    txt_senha.requestFocus();
                } catch(FirebaseAuthInvalidCredentialsException e) {
                    Toast.makeText(getApplication().getApplicationContext(), "Erro: email invalido" , Toast.LENGTH_SHORT).show();
                    txt_email.setError("Email Invalido");
                    txt_email.requestFocus();
                } catch(FirebaseAuthUserCollisionException e) {
                    Toast.makeText(getApplication().getApplicationContext(), "Erro: email ja cadastrado" , Toast.LENGTH_SHORT).show();
                    txt_email.setError("Email ja cadastrado");
                    txt_email.setText("");
                    txt_email.requestFocus();
                } catch(Exception e) {
                    Log.i("aqui meu", e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(getApplication().getApplicationContext(), "Erro ao Cadastrar Usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean retorno = false;
        if (manager.getActiveNetworkInfo() != null  && manager.getActiveNetworkInfo().isAvailable()
                    && manager.getActiveNetworkInfo().isConnected()) {
            retorno  = true;
        }
        if (!retorno) {
            Toast.makeText(getApplication().getApplicationContext(), "Não Foi possivel conectar-se a internet", Toast.LENGTH_SHORT).show();
        }
        return retorno;
    }

    public boolean validaData(String s) {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        String data = s;
        try {
            Date date = sdf.parse(data);
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return  false;
        }
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
