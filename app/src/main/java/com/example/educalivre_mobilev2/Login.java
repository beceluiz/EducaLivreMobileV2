package com.example.educalivre_mobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.SQLException;

public class Login extends AppCompatActivity {
    Acessa objA = new Acessa();
    EditText username, password;
    TextView erroText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        erroText = findViewById(R.id.erroText);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
    }

    public void logar(View V) {
        objA.entBanco(this);
        String nome = username.getText().toString();
        String senha = password.getText().toString();
        try {
            objA.RS = objA.stmt.executeQuery("SELECT * FROM usuario WHERE email='"+nome+"' AND senha='"+senha+"'");
            if(objA.RS.next()){
                int idUsuarioLogado = objA.RS.getInt("idUsuario");
                String nomeUsuarioLogado = objA.RS.getString("nome");
                String sobrenomeUsuarioLogado = objA.RS.getString("sobrenome");
                String emailUsuarioLogado = objA.RS.getString("email");
                String celularUsuarioLogado = objA.RS.getString("celular");
                String generoUsuarioLogado = objA.RS.getString("genero");
                String senhaUsuarioLogado = objA.RS.getString("senha");
                String dataCriacaoUsuarioLogado = objA.RS.getString("dataCriacao");

                Usuario.setId(idUsuarioLogado);
                Usuario.setNome(nomeUsuarioLogado);
                Usuario.setSobrenome(sobrenomeUsuarioLogado);
                Usuario.setEmail(emailUsuarioLogado);
                Usuario.setCelular(celularUsuarioLogado);
                Usuario.setGenero(generoUsuarioLogado);
                Usuario.setSenha(senhaUsuarioLogado);
                Usuario.setDataCriacao(dataCriacaoUsuarioLogado);

                //Toast.makeText(getApplicationContext(), "Aprovado", Toast.LENGTH_SHORT).show();
                Intent Intent = new Intent(Login.this, PaginaHome.class);
                startActivity(Intent);
                finish();
            } else {
                //Toast.makeText(getApplicationContext(), "Entrada Não Aprovada",Toast.LENGTH_SHORT).show();
                erroText.setText("Usuario ou Senha inválido!");
                erroText.setVisibility(View.VISIBLE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Log.e("Erro: ", ex.getMessage());
        }
    }

    public void irParaCadastrar (View V) {
        Intent intent = new Intent(Login.this, Cadastro.class);
        startActivity(intent);
    }
}