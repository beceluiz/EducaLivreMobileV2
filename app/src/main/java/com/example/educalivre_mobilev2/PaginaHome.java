package com.example.educalivre_mobilev2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.SQLException;

public class PaginaHome extends AppCompatActivity {
    Acessa objA = new Acessa();

    private TextView tvBoasVindas, txtSimuladosFeitos, txtPorcentagemSimulados;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pagina_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        objA.entBanco(this);
        // Inicializar componentes
        initComponents();
        setupSimuladoData();

        // Conectar ao banco

        // Configurar mensagem personalizada
        setupWelcomeMessage();
    }

    private void initComponents() {
        tvBoasVindas = findViewById(R.id.tvBoasVindas);
        txtSimuladosFeitos = findViewById(R.id.txtSimuladosFeitos);
        txtPorcentagemSimulados = findViewById(R.id.txtAproveitamento);
    }

    private void setupWelcomeMessage() {
        // Verificar se há nome do usuário armazenado
        String nomeUsuario = Usuario.getNome();

        if (nomeUsuario != null && !nomeUsuario.isEmpty()) {
            tvBoasVindas.setText("Olá, " + nomeUsuario + "! 👋");
        } else {
            tvBoasVindas.setText("Olá! 👋");
        }
    }

    private void setupSimuladoData() {
        int idUsuario = Usuario.getId();
        try {
            objA.RS = objA.stmt.executeQuery("SELECT COUNT(id_simulado) as total_simulados FROM simulado WHERE idUsuario=" + idUsuario );

            if(objA.RS.next()){
                txtSimuladosFeitos.setText(objA.RS.getInt("total_simulados") + "");
            } else {
                txtSimuladosFeitos.setText("0");
            }

            } catch (SQLException e) {
            Log.e("Simulado realzizados", "Erro ao ver dadoss de simulado realizado: " + e.getMessage(), e);
        }

        try {
            objA.RS = objA.stmt.executeQuery("SELECT AVG(percentual_acerto) as total_porcentagem FROM simulado WHERE idUsuario = " + idUsuario);

            if(objA.RS.next()){

                txtPorcentagemSimulados.setText(objA.RS.getDouble("total_porcentagem") + "");
            } else {
                txtPorcentagemSimulados.setText("0");
            }

        } catch (SQLException e) {
            Log.e("Simulado realzizados", "Erro ao ver dadoss de simulado realizado: " + e.getMessage(), e);
        }
    }


    public void irParaDashboardUsuario(View v) {
        Intent intent = new Intent(getApplicationContext(), DashboardUsuario.class);
        startActivity(intent);
        finish();
    }


    public void irParaSimulado(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Iniciar novo Simulado?")
                .setMessage("Tem certeza que deseja iniciar um novo Simulado? Essa ação não pode ser desfeita.")
                .setPositiveButton("Sim, Iniciar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), Simulado.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Método para ir para histórico de simulados
    public void irParHistotricoSimulados(View v) {
        Intent intent = new Intent(PaginaHome.this, HistoricoSimulados.class);
        startActivity(intent);
    }

    // Novo método para videoaulas (abre link externo)
    public void irParaVideoaulas(View v) {
        // Substitua pela URL do seu site de videoaulas
        String url = "http://localhost/TCC/materias.php";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        // Verificar se há um aplicativo que pode abrir o link
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualizar mensagem de boas-vindas quando voltar para a tela
        setupWelcomeMessage();
    }
}