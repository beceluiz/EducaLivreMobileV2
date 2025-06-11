package com.example.educalivre_mobilev2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Resultados extends AppCompatActivity {
    private TextView txtAcertos, txtTotal, txtPercentual, txtAno, txtTempo, txtStatus;
    private Button btnVerDetalhes, btnNovoSimulado, btnVoltar;
    private ProgressBar progressBarSalvando;

    private int acertos;
    private int total;
    private int anoProva;
    private ArrayList<String> respostasUsuario;
    private ArrayList<String> respostasCorretas;
    private ArrayList<String> disciplinas;

    private Acessa acessa;
    private int idUsuario = Usuario.getId();
    private int idSimuladoSalvo = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultados);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtAcertos = findViewById(R.id.txtAcertos);
        txtTotal = findViewById(R.id.txtTotal);
        txtPercentual = findViewById(R.id.txtPercentual);
        txtAno = findViewById(R.id.txtAno);
        txtTempo = findViewById(R.id.txtTempo);
        txtStatus = findViewById(R.id.txtStatus);

        btnVerDetalhes = findViewById(R.id.btnVerDetalhes);
        btnNovoSimulado = findViewById(R.id.btnNovoSimulado);
        btnVoltar = findViewById(R.id.btnVoltar);

        progressBarSalvando = findViewById(R.id.progressBarSalvando);

        getIntentData();
        setupClickListeners();

        acessa = new Acessa();

        // Salvar resultados no banco de dados
        new SalvarResultadosTask().execute();
    }
    private void getIntentData() {
        Intent intent = getIntent();
        acertos = intent.getIntExtra("acertos", 0);
        total = intent.getIntExtra("total", 0);
        anoProva = intent.getIntExtra("ano", 0);
        respostasUsuario = intent.getStringArrayListExtra("respostasUsuario");
        respostasCorretas = intent.getStringArrayListExtra("respostasCorretas");
        disciplinas = intent.getStringArrayListExtra("disciplinas");
    }

    private void setupClickListeners() {
        btnVerDetalhes.setOnClickListener(v -> verDetalhes());
        btnNovoSimulado.setOnClickListener(v -> novoSimulado());
        btnVoltar.setOnClickListener(v -> voltarMenu());
    }

    private void displayResults() {
        double percentual = total > 0 ? (double) acertos / total * 100 : 0;

        txtAcertos.setText(String.valueOf(acertos));
        txtTotal.setText(String.valueOf(total));
        txtPercentual.setText(String.format("%.1f%%", percentual));
        txtAno.setText("ENEM " + anoProva);

        String status;
        if (percentual >= 70) {
            status = "Excelente!";
            txtStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if (percentual >= 50) {
            status = "Bom desempenho!";
            txtStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            status = "Continue estudando!";
            txtStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
        txtStatus.setText(status);
    }

    private void verDetalhes() {
        if (idSimuladoSalvo != -1) {
            Intent intent = new Intent(this, Detalhes_Simulado.class);
            intent.putExtra("idSimulado", idSimuladoSalvo);
            intent.putExtra("acertos", acertos);
            intent.putExtra("total", total);
            intent.putExtra("anoProva", anoProva);
            intent.putStringArrayListExtra("respostasUsuario", respostasUsuario);
            intent.putStringArrayListExtra("respostasCorretas", respostasCorretas);
            intent.putStringArrayListExtra("disciplinas", disciplinas);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Aguarde o salvamento dos dados", Toast.LENGTH_SHORT).show();
        }
    }

    private void novoSimulado() {
        Intent intent = new Intent(this, Simulado.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void voltarMenu() {
        // Voltar para a activity principal ou menu
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private class SalvarResultadosTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressBarSalvando.setVisibility(View.VISIBLE);
            btnVerDetalhes.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Connection connection = acessa.entBanco(Resultados.this);
                if (connection == null) {
                    return false;
                }

                // Salvar simulado principal
                if (salvarSimulado(connection)) {
                    // Salvar respostas detalhadas
                    salvarRespostasDetalhadas(connection);
                    return true;
                }

                return false;

            } catch (Exception e) {
                Log.e("SALVAR_RESULTADOS", "Erro ao salvar resultados", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            progressBarSalvando.setVisibility(View.GONE);
            btnVerDetalhes.setEnabled(true);

            if (sucesso) {
                Toast.makeText(Resultados.this, "Resultados salvos com sucesso!", Toast.LENGTH_SHORT).show();
                displayResults();
            } else {
                Toast.makeText(Resultados.this, "Erro ao salvar resultados", Toast.LENGTH_LONG).show();
                // Mesmo com erro, mostrar os resultados
                displayResults();
            }
        }
    }

    private boolean salvarSimulado(Connection connection) {
        try {
            double percentual = total > 0 ? (double) acertos / total * 100 : 0;

            String sql = "INSERT INTO simulado (ano_prova_simulado, idUsuario, total_questions, " +
                    "total_correct_questions, percentual_acerto, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql,
                    PreparedStatement.RETURN_GENERATED_KEYS);

            statement.setInt(1, anoProva);
            statement.setInt(2, idUsuario);
            statement.setInt(3, total);
            statement.setInt(4, acertos);
            statement.setDouble(5, percentual);
            statement.setString(6, "CONCLUIDO");

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Pegar o ID do simulado inserido
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    idSimuladoSalvo = generatedKeys.getInt(1);
                }

                Log.d("SALVAR_SIMULADO", "Simulado salvo com ID: " + idSimuladoSalvo);
                return true;
            }

        } catch (SQLException e) {
            Log.e("SALVAR_SIMULADO", "Erro SQL ao salvar simulado", e);
        }

        return false;
    }

    private void salvarRespostasDetalhadas(Connection connection) {
        if (idSimuladoSalvo == -1 || respostasUsuario == null || respostasCorretas == null) {
            return;
        }

        try {
            String sql = "INSERT INTO simulado_resposta (id_simulado, numero_questao, " +
                    "resposta_usuario, resposta_correta, acertou, disciplina) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            for (int i = 0; i < respostasUsuario.size(); i++) {
                String respostaUsuario = respostasUsuario.get(i);
                String respostaCorreta = respostasCorretas.get(i);
                String disciplina = disciplinas != null && i < disciplinas.size() ?
                        disciplinas.get(i) : "N/A";

                boolean acertou = respostaUsuario.equalsIgnoreCase(respostaCorreta);

                statement.setInt(1, idSimuladoSalvo);
                statement.setInt(2, i + 1); // número da questão (1-based)
                statement.setString(3, respostaUsuario);
                statement.setString(4, respostaCorreta);
                statement.setBoolean(5, acertou);
                statement.setString(6, disciplina);

                statement.addBatch();
            }

            int[] results = statement.executeBatch();
            Log.d("SALVAR_RESPOSTAS", "Salvas " + results.length + " respostas detalhadas");

        } catch (SQLException e) {
            Log.e("SALVAR_RESPOSTAS", "Erro ao salvar respostas detalhadas", e);
        }
    }
}