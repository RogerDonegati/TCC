package com.tcc.zeugmaproject.zeugmaproject.Control;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ConteudoAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Adapters.ListasAdapter;
import com.tcc.zeugmaproject.zeugmaproject.Model.Conteudo;
import com.tcc.zeugmaproject.zeugmaproject.Model.Usuario;
import com.tcc.zeugmaproject.zeugmaproject.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfiguracaoActivity extends AppCompatActivity {
    private DatabaseReference firabaseReference= FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth;
    private TextView txt_nome;
    private TextView txt_apelido;
    private TextView txt_nascimento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);


        firebaseAuth = FirebaseAuth.getInstance();
        Button btn_redefinir = (Button) findViewById(R.id.btn_config_redefinir_senha);
        Button btn_confirmar = (Button) findViewById(R.id.btn_config_confirmar);
        txt_nome = (TextView) findViewById(R.id.txt_config_nome);
        txt_apelido = (TextView) findViewById(R.id.txt_config_apelido);
        txt_nascimento = (TextView) findViewById(R.id.txt_config_nascimento);
        firabaseReference.child("usuario").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EditText txt_email = (EditText) findViewById(R.id.txt_config_email);
                Usuario usuario;
                usuario = dataSnapshot.getValue(Usuario.class);
                txt_nome.setText(usuario.getNome());
                txt_nascimento.setText(usuario.getNascimento());
                txt_apelido.setText(usuario.getApelido());
                txt_email.setText(usuario.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_redefinir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            firebaseAuth.sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail() )
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    if( task.isSuccessful() ){
                        Toast.makeText(ConfiguracaoActivity.this, "Email de redefinição enviado para " + firebaseAuth.getCurrentUser().getEmail() ,
                                Toast.LENGTH_SHORT
                        ).show();
                    }else{
                        Toast.makeText(ConfiguracaoActivity.this, "Não foi possivel conectar-se ao servidor! Tente novamente",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("aqui meu", e.getMessage() + " " + e);
                    }
                });
            }
        });

        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                String nome = txt_nome.getText().toString();
                                String apelido = txt_apelido.getText().toString();
                                String nascimento = txt_nascimento.getText().toString();
                                if (validaDados(nome,apelido,txt_nascimento)){
                                    Map<String, Object> userUpdates = new HashMap<>();
                                    userUpdates.put(firebaseAuth.getCurrentUser().getUid() + "/nome", nome);
                                    userUpdates.put(firebaseAuth.getCurrentUser().getUid() + "/apelido", apelido);
                                    userUpdates.put(firebaseAuth.getCurrentUser().getUid() + "/nascimento", nascimento);
                                    firabaseReference.child("usuario").updateChildren(userUpdates);
                                    Toast.makeText(getApplication().getApplicationContext(), "Cadastro atualizado!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfiguracaoActivity.this);
                builder.setMessage("Você deseja realmente atualizar seu Cadastro?").setPositiveButton("Sim", dialogClickListener)
                        .setNegativeButton("Não", dialogClickListener).show();
            }
        });

    }

    public boolean validaDados(String nome, String apelido, TextView nascimento) {
        Registrar controle = new Registrar();
        if (nascimento.getText().toString().isEmpty() || nome.isEmpty() || apelido.isEmpty()) {
            Toast.makeText(getApplication().getApplicationContext(), "Favor Preencher todos os campos", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!controle.validaData(nascimento.getText().toString())) {
            nascimento.setError("Nascimento invalido");
            Toast.makeText(this, "Nascimento invalido", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
