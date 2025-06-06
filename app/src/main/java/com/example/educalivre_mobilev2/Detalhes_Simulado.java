package com.example.educalivre_mobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Detalhes_Simulado extends AppCompatActivity {
    private RecyclerView recyclerViewDetalhes;
    private TextView txtResumoGeral, txtEstatisticasDisciplina;

    private int idSimulado;
    private int acertos;
    private int total;
    private int anoProva;
    private ArrayList<String> respostasUsuario;
    private ArrayList<String> respostasCorretas;
    private ArrayList<String> disciplinas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhes_simulado);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerViewDetalhes = findViewById(R.id.recyclerViewDetalhes);
        txtResumoGeral = findViewById(R.id.txtResumoGeral);
        txtEstatisticasDisciplina = findViewById(R.id.txtEstatisticasDisciplina);

        // Setup toolbar if needed
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalhes do Simulado");
        }

        getIntentData();
        setupRecyclerView();
        calculateStatistics();
    }
    private void initializeViews() {
        recyclerViewDetalhes = findViewById(R.id.recyclerViewDetalhes);
        txtResumoGeral = findViewById(R.id.txtResumoGeral);
        txtEstatisticasDisciplina = findViewById(R.id.txtEstatisticasDisciplina);

        // Setup toolbar if needed
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalhes do Simulado");
        }
    }

    private void getIntentData() {
        Intent intent = getIntent();
        idSimulado = intent.getIntExtra("idSimulado", -1);
        acertos = intent.getIntExtra("acertos", 0);
        total = intent.getIntExtra("total", 0);
        anoProva = intent.getIntExtra("anoProva", 0);
        respostasUsuario = intent.getStringArrayListExtra("respostasUsuario");
        respostasCorretas = intent.getStringArrayListExtra("respostasCorretas");
        disciplinas = intent.getStringArrayListExtra("disciplinas");
    }

    private void setupRecyclerView() {
        List<ItemDetalhe> itensDetalhes = new ArrayList<>();

        for (int i = 0; i < respostasUsuario.size(); i++) {
            ItemDetalhe item = new ItemDetalhe();
            item.numeroQuestao = i + 1;
            item.respostaUsuario = respostasUsuario.get(i);
            item.respostaCorreta = respostasCorretas.get(i);
            item.disciplina = disciplinas != null && i < disciplinas.size() ? disciplinas.get(i) : "N/A";
            item.acertou = item.respostaUsuario.equalsIgnoreCase(item.respostaCorreta);

            itensDetalhes.add(item);
        }

        DetalhesAdapter adapter = new DetalhesAdapter(itensDetalhes);
        recyclerViewDetalhes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDetalhes.setAdapter(adapter);
    }

    private void calculateStatistics() {
        // Resumo geral
        double percentual = total > 0 ? (double) acertos / total * 100 : 0;
        String resumo = String.format("ENEM %d - %d de %d questões corretas (%.1f%%)",
                anoProva, acertos, total, percentual);
        txtResumoGeral.setText(resumo);

        // Estatísticas por disciplina
        if (disciplinas != null) {
            Map<String, Integer> acertosPorDisciplina = new HashMap<>();
            Map<String, Integer> totalPorDisciplina = new HashMap<>();

            for (int i = 0; i < disciplinas.size(); i++) {
                String disciplina = disciplinas.get(i);
                boolean acertou = respostasUsuario.get(i).equalsIgnoreCase(respostasCorretas.get(i));

                totalPorDisciplina.put(disciplina, totalPorDisciplina.getOrDefault(disciplina, 0) + 1);
                if (acertou) {
                    acertosPorDisciplina.put(disciplina, acertosPorDisciplina.getOrDefault(disciplina, 0) + 1);
                }
            }

            StringBuilder estatisticas = new StringBuilder("📊 Desempenho por Disciplina:\n\n");
            for (String disciplina : totalPorDisciplina.keySet()) {
                int acertosDisc = acertosPorDisciplina.getOrDefault(disciplina, 0);
                int totalDisc = totalPorDisciplina.get(disciplina);
                double percentualDisc = (double) acertosDisc / totalDisc * 100;

                estatisticas.append(String.format("• %s: %d/%d (%.1f%%)\n",
                        disciplina, acertosDisc, totalDisc, percentualDisc));
            }

            txtEstatisticasDisciplina.setText(estatisticas.toString());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Classe para representar um item de detalhe
    private static class ItemDetalhe {
        int numeroQuestao;
        String respostaUsuario;
        String respostaCorreta;
        String disciplina;
        boolean acertou;
    }

    // Adapter para o RecyclerView
    private class DetalhesAdapter extends RecyclerView.Adapter<DetalhesAdapter.ViewHolder> {

        private List<ItemDetalhe> itens;

        public DetalhesAdapter(List<ItemDetalhe> itens) {
            this.itens = itens;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_detalhe_questao, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ItemDetalhe item = itens.get(position);

            holder.txtNumeroQuestao.setText("Questão " + item.numeroQuestao);
            holder.txtDisciplina.setText(item.disciplina);
            holder.txtRespostaUsuario.setText("Sua resposta: " + item.respostaUsuario);
            holder.txtRespostaCorreta.setText("Resposta correta: " + item.respostaCorreta);

            // Definir cores baseadas no acerto
            if (item.acertou) {
                holder.txtStatus.setText("✅ CORRETO");
                holder.txtStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                holder.itemView.setBackgroundColor(getResources().getColor(android.R.color.white));
            } else {
                holder.txtStatus.setText("❌ INCORRETO");
                holder.txtStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                holder.itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light, null));
                holder.itemView.getBackground().setAlpha(30); // Transparência
            }
        }

        @Override
        public int getItemCount() {
            return itens.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtNumeroQuestao, txtDisciplina, txtRespostaUsuario, txtRespostaCorreta, txtStatus;

            public ViewHolder(View itemView) {
                super(itemView);
                txtNumeroQuestao = itemView.findViewById(R.id.txtNumeroQuestao);
                txtDisciplina = itemView.findViewById(R.id.txtDisciplina);
                txtRespostaUsuario = itemView.findViewById(R.id.txtRespostaUsuario);
                txtRespostaCorreta = itemView.findViewById(R.id.txtRespostaCorreta);
                txtStatus = itemView.findViewById(R.id.txtStatus);
            }
        }
    }
}