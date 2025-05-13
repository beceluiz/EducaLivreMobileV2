package com.example.educalivre_mobilev2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.SQLException;

public class DashboardUsuario extends AppCompatActivity {

    EditText nomeUsuario, sobrenomeUsuario, emailUsuario, celularUsuario;
    TextView dataCriacaoUsuario, statusUsuario;
    Button atualizarDados;
    Acessa ObjA = new Acessa();
    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        atualizarDados = findViewById(R.id.atualizarDados);

        nomeUsuario = findViewById(R.id.nomeUsuario);
        sobrenomeUsuario = findViewById(R.id.sobrenomeUsuario);
        emailUsuario = findViewById(R.id.emailUsuario);
        celularUsuario = findViewById(R.id.celularUsuario);
        dataCriacaoUsuario = findViewById(R.id.dataCriacaoUsuario);
        statusUsuario = findViewById(R.id.statusUsuario);

        nomeUsuario.setText(Usuario.getNome());
        sobrenomeUsuario.setText(Usuario.getSobrenome());
        emailUsuario.setText(Usuario.getEmail());
        celularUsuario.setText(Usuario.getCelular());
        dataCriacaoUsuario.setText(Usuario.getDataCriacao());

        if(Usuario.getStatus() == 1) {
            statusUsuario.setText("Administrador");
        } else {
            statusUsuario.setText("Usuario Comum");
        }

        System.out.println(Usuario.getNome());
    }
    public void atualizarDados(View v) {
        if (state == 0) {
            nomeUsuario.setEnabled(true);
            sobrenomeUsuario.setEnabled(true);
            emailUsuario.setEnabled(true);
            celularUsuario.setEnabled(true);

            nomeUsuario.setAlpha(1.0f);
            sobrenomeUsuario.setAlpha(1.0f);
            emailUsuario.setAlpha(1.0f);
            celularUsuario.setAlpha(1.0f);

            int textColorAtivo = Color.parseColor("#000000");

            nomeUsuario.setTextColor(textColorAtivo);
            sobrenomeUsuario.setTextColor(textColorAtivo);
            emailUsuario.setTextColor(textColorAtivo);
            celularUsuario.setTextColor(textColorAtivo);

            atualizarDados.setText("Confirmar Alterações");
            state = 1;
        } else if (state == 1) {
            ObjA.entBanco(this);

            nomeUsuario.setEnabled(false);
            sobrenomeUsuario.setEnabled(false);
            emailUsuario.setEnabled(false);
            celularUsuario.setEnabled(false);
            atualizarDados.setText("Atualizar seus Dados");
            try {
                int linhasAfetadas = ObjA.stmt.executeUpdate("UPDATE Usuario SET nome = '"+nomeUsuario.getText()+"', sobrenome = '"+sobrenomeUsuario.getText()+"', email = '"+emailUsuario.getText()+"', celular = '"+celularUsuario.getText()+"' WHERE idUsuario = '"+Usuario.getId()+"'");
                if (linhasAfetadas > 0) {
                    Usuario.setNome("" + nomeUsuario.getText());
                    Usuario.setSobrenome("" + sobrenomeUsuario.getText());
                    Usuario.setEmail("" + emailUsuario.getText());
                    Usuario.setCelular("" + celularUsuario.getText());
                    Toast.makeText(getApplicationContext(), "Alterado com Sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Nenhuma Alteração Realizada!" + Usuario.getNome(), Toast.LENGTH_SHORT).show();

                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                Log.e("Erro: ", ex.getMessage());
            }
            state = 0;
            Intent intent = new Intent(DashboardUsuario.this, PaginaHome.class);
            startActivity(intent);
            finish();
        }
    }
}