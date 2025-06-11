package com.example.educalivre_mobilev2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoricoSimulados extends AppCompatActivity {
    private RecyclerView recyclerViewHistorico;
    private ProgressBar progressBarCarregando;
    private LinearLayout txtSemSimulados;

    private List<SimuladoItem> simulados;
    private HistoricoAdapter adapter;
    private Acessa acessa;
    private int idUsuario = Usuario.getId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historico_simulados);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerViewHistorico = findViewById(R.id.recyclerViewHistorico);
        progressBarCarregando = findViewById(R.id.progressBarCarregando);
        txtSemSimulados = findViewById(R.id.txtSemSimulados);

        simulados = new ArrayList<>();
        adapter = new HistoricoAdapter(simulados);
        recyclerViewHistorico.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistorico.setAdapter(adapter);

        acessa = new Acessa();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Histórico de Simulados");
        }

        // Carregar simulados do usuário
        new CarregarSimuladosTask().execute();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Classe para representar um item do histórico
    public static class SimuladoItem {
        private int idSimulado;
        private int anoProva;
        private int totalQuestoes;
        private int acertos;
        private double percentualAcerto;
        private String dataSimulado;
        private String status;

        // Getters e Setters
        public int getIdSimulado() { return idSimulado; }
        public void setIdSimulado(int idSimulado) { this.idSimulado = idSimulado; }

        public int getAnoProva() { return anoProva; }
        public void setAnoProva(int anoProva) { this.anoProva = anoProva; }

        public int getTotalQuestoes() { return totalQuestoes; }
        public void setTotalQuestoes(int totalQuestoes) { this.totalQuestoes = totalQuestoes; }

        public int getAcertos() { return acertos; }
        public void setAcertos(int acertos) { this.acertos = acertos; }

        public double getPercentualAcerto() { return percentualAcerto; }
        public void setPercentualAcerto(double percentualAcerto) { this.percentualAcerto = percentualAcerto; }

        public String getDataSimulado() { return dataSimulado; }
        public void setDataSimulado(String dataSimulado) { this.dataSimulado = dataSimulado; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // Adapter para o RecyclerView
    private class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.ViewHolder> {
        private List<SimuladoItem> itens;

        public HistoricoAdapter(List<SimuladoItem> itens) {
            this.itens = itens;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_historico_simulado, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SimuladoItem item = itens.get(position);

            holder.txtAnoProva.setText("ENEM " + item.getAnoProva());
            holder.txtDataSimulado.setText(formatarData(item.getDataSimulado()));
            holder.txtResultado.setText(String.format(Locale.getDefault(),
                    "%d/%d questões (%.1f%%)",
                    item.getAcertos(),
                    item.getTotalQuestoes(),
                    item.getPercentualAcerto()));

            // Definir cor baseada no desempenho
            if (item.getPercentualAcerto() >= 70) {
                holder.txtPercentual.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                holder.txtStatus.setText("Excelente!");
                holder.txtStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else if (item.getPercentualAcerto() >= 50) {
                holder.txtPercentual.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                holder.txtStatus.setText("Bom");
                holder.txtStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                holder.txtPercentual.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                holder.txtStatus.setText("Precisa melhorar");
                holder.txtStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            holder.txtPercentual.setText(String.format(Locale.getDefault(), "%.1f%%", item.getPercentualAcerto()));

            // Click listener para abrir detalhes
            holder.itemView.setOnClickListener(v -> abrirDetalhes(item));
        }

        @Override
        public int getItemCount() {
            return itens.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtAnoProva, txtDataSimulado, txtResultado, txtPercentual, txtStatus;

            public ViewHolder(View itemView) {
                super(itemView);
                txtAnoProva = itemView.findViewById(R.id.txtAnoProva);
                txtDataSimulado = itemView.findViewById(R.id.txtDataSimulado);
                txtResultado = itemView.findViewById(R.id.txtResultado);
                txtPercentual = itemView.findViewById(R.id.txtPercentual);
                txtStatus = itemView.findViewById(R.id.txtStatus);
            }
        }
    }

    private void abrirDetalhes(SimuladoItem item) {
        // Carregar respostas detalhadas e abrir tela de detalhes
        new CarregarDetalhesTask(item).execute();
    }

    private String formatarData(String dataString) {
        try {
            // Assumindo que a data vem no formato do SQL Server
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date data = inputFormat.parse(dataString);
            return outputFormat.format(data);
        } catch (Exception e) {
            Log.e("FORMATACAO_DATA", "Erro ao formatar data: " + dataString, e);
            return dataString; // Retorna a string original se houver erro
        }
    }

    // AsyncTask para carregar simulados do usuário
    private class CarregarSimuladosTask extends AsyncTask<Void, Void, List<SimuladoItem>> {

        @Override
        protected void onPreExecute() {
            progressBarCarregando.setVisibility(View.VISIBLE);
            recyclerViewHistorico.setVisibility(View.GONE);
            txtSemSimulados.setVisibility(View.GONE);
        }

        @Override
        protected List<SimuladoItem> doInBackground(Void... voids) {
            List<SimuladoItem> resultado = new ArrayList<>();

            try {
                Connection connection = acessa.entBanco(HistoricoSimulados.this);
                if (connection == null) {
                    return resultado;
                }

                String sql = "SELECT id_simulado, ano_prova_simulado, total_questions, " +
                        "total_correct_questions, percentual_acerto, data_simulado, status " +
                        "FROM simulado WHERE idUsuario = ? " +
                        "ORDER BY data_simulado DESC";

                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, idUsuario);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    SimuladoItem item = new SimuladoItem();
                    item.setIdSimulado(resultSet.getInt("id_simulado"));
                    item.setAnoProva(resultSet.getInt("ano_prova_simulado"));
                    item.setTotalQuestoes(resultSet.getInt("total_questions"));
                    item.setAcertos(resultSet.getInt("total_correct_questions"));
                    item.setPercentualAcerto(resultSet.getDouble("percentual_acerto"));
                    item.setDataSimulado(resultSet.getString("data_simulado"));
                    item.setStatus(resultSet.getString("status"));

                    resultado.add(item);
                }

                resultSet.close();
                statement.close();
                connection.close();

            } catch (SQLException e) {
                Log.e("CARREGAR_SIMULADOS", "Erro ao carregar simulados", e);
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(List<SimuladoItem> resultado) {
            progressBarCarregando.setVisibility(View.GONE);

            if (resultado.isEmpty()) {
                txtSemSimulados.setVisibility(View.VISIBLE);
                recyclerViewHistorico.setVisibility(View.GONE);
            } else {
                simulados.clear();
                simulados.addAll(resultado);
                adapter.notifyDataSetChanged();

                recyclerViewHistorico.setVisibility(View.VISIBLE);
                txtSemSimulados.setVisibility(View.GONE);

                Toast.makeText(HistoricoSimulados.this,
                        "Carregados " + resultado.size() + " simulados",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask para carregar detalhes de um simulado específico
    private class CarregarDetalhesTask extends AsyncTask<Void, Void, Boolean> {
        private SimuladoItem simuladoItem;
        private ArrayList<String> respostasUsuario;
        private ArrayList<String> respostasCorretas;
        private ArrayList<String> disciplinas;

        public CarregarDetalhesTask(SimuladoItem item) {
            this.simuladoItem = item;
            this.respostasUsuario = new ArrayList<>();
            this.respostasCorretas = new ArrayList<>();
            this.disciplinas = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            progressBarCarregando.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Connection connection = acessa.entBanco(HistoricoSimulados.this);
                if (connection == null) {
                    return false;
                }

                String sql = "SELECT numero_questao, resposta_usuario, resposta_correta, disciplina " +
                        "FROM simulado_resposta WHERE id_simulado = ? " +
                        "ORDER BY numero_questao";

                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, simuladoItem.getIdSimulado());

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    respostasUsuario.add(resultSet.getString("resposta_usuario"));
                    respostasCorretas.add(resultSet.getString("resposta_correta"));
                    disciplinas.add(resultSet.getString("disciplina"));
                }

                resultSet.close();
                statement.close();
                connection.close();

                return !respostasUsuario.isEmpty();

            } catch (SQLException e) {
                Log.e("CARREGAR_DETALHES", "Erro ao carregar detalhes do simulado", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            progressBarCarregando.setVisibility(View.GONE);

            if (sucesso) {
                Intent intent = new Intent(HistoricoSimulados.this, Detalhes_Simulado.class);
                intent.putExtra("idSimulado", simuladoItem.getIdSimulado());
                intent.putExtra("acertos", simuladoItem.getAcertos());
                intent.putExtra("total", simuladoItem.getTotalQuestoes());
                intent.putExtra("anoProva", simuladoItem.getAnoProva());
                intent.putStringArrayListExtra("respostasUsuario", respostasUsuario);
                intent.putStringArrayListExtra("respostasCorretas", respostasCorretas);
                intent.putStringArrayListExtra("disciplinas", disciplinas);

                startActivity(intent);
            } else {
                Toast.makeText(HistoricoSimulados.this,
                        "Erro ao carregar detalhes do simulado",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}