package com.example.educalivre_mobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.noties.markwon.Markwon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Simulado extends AppCompatActivity {
    private TextView textTitulo, textContexto, txtDisciplina, txtIntroAlternativas;
    private ImageView imageView;
    private RadioGroup radioGroup;
    private Button buttonResponder;
    private ProgressBar progressBarSimulado;

    private List<Questao> questoes;
    private int questaoAtual = 0;
    private int acertos = 0;

    // Array to store user's answers and correct answers for final review
    private List<String> respostasUsuario;
    private List<String> respostasCorretas;
    private List<String> disciplinas; // Store disciplines for each question

    // Variáveis para paginação e anos disponíveis
    Random random = new Random();
    int[] anosProvasDisponiveis = {2009,2010,2011,2012,2013,2014,2015,2016,2017,2018,2019,2020,2021,2022,2023};
    private int anoEscolhido;
    private int limit = 50;
    private int offset = 0;
    private boolean carregandoQuestoes = false;
    private int totalQuestoes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_simulado);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textTitulo = findViewById(R.id.textTitulo);
        textContexto = findViewById(R.id.textContexto);
        imageView = findViewById(R.id.imageViewQuestao);
        radioGroup = findViewById(R.id.radioGroupAlternativas);
        buttonResponder = findViewById(R.id.buttonContinuar);
        txtDisciplina = findViewById(R.id.txtDisciplina);
        txtIntroAlternativas = findViewById(R.id.txtIntroAlternativas);

        progressBarSimulado = findViewById(R.id.progressBarSimulado);
        if (progressBarSimulado != null) {
            progressBarSimulado.setVisibility(View.VISIBLE);
        }

        questoes = new ArrayList<>();
        respostasUsuario = new ArrayList<>();
        respostasCorretas = new ArrayList<>();
        disciplinas = new ArrayList<>();
        anoEscolhido = anosProvasDisponiveis[sortearAnoProva()];
        carregarTodasQuestoes();

        buttonResponder.setOnClickListener(view -> verificarResposta());
    }

    private void carregarTodasQuestoes() {
        if (carregandoQuestoes) return;

        carregandoQuestoes = true;

        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.enem.dev/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService api = retrofit.create(ApiService.class);
            Call<RespostaAPI> call = api.listarQuestoesPaginadas(anoEscolhido, limit, offset);

            call.enqueue(new Callback<RespostaAPI>() {
                @Override
                public void onResponse(Call<RespostaAPI> call, Response<RespostaAPI> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            RespostaAPI resposta = response.body();
                            Metadata metadata = resposta.getMetadata();
                            List<Questao> novasQuestoes = resposta.getQuestions();

                            if (novasQuestoes != null && !novasQuestoes.isEmpty()) {
                                questoes.addAll(novasQuestoes);
                                totalQuestoes = metadata.getTotal();

                                Log.d("PAGINACAO", "Carregadas " + novasQuestoes.size() +
                                        " questões. Total atual: " + questoes.size() +
                                        " de " + totalQuestoes);

                                if (metadata.isHasMore() && questoes.size() < totalQuestoes) {
                                    offset += limit;
                                    carregandoQuestoes = false;
                                    carregarTodasQuestoes();
                                } else {
                                    finalizarCarregamento();
                                }
                            } else {
                                finalizarCarregamento();
                            }
                        } else {
                            String erro = "Código HTTP: " + response.code();
                            Log.e("API_RESPONSE", "Erro na resposta da API: " + erro);
                            mostrarErro("Erro ao carregar questões. " + erro);
                        }
                    } catch (Exception e) {
                        Log.e("API_RESPONSE", "Erro ao processar resposta", e);
                        mostrarErro("Erro ao processar dados: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<RespostaAPI> call, Throwable t) {
                    Log.e("API_RESPONSE", "Falha ao conectar com a API", t);
                    mostrarErro("Falha de conexão: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("API_INIT", "Erro ao inicializar Retrofit", e);
            mostrarErro("Erro ao inicializar requisição: " + e.getMessage());
        }
    }

    private void finalizarCarregamento() {
        carregandoQuestoes = false;

        if (progressBarSimulado != null) {
            progressBarSimulado.setVisibility(View.GONE);
        }

        if (questoes.isEmpty()) {
            Toast.makeText(this, "Nenhuma questão encontrada.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Log.d("CARREGAMENTO", "Carregamento finalizado. Total de questões: " + questoes.size());
        Toast.makeText(this, "Carregadas " + questoes.size() + " questões do ENEM " + anoEscolhido + "!", Toast.LENGTH_SHORT).show();

        mostrarQuestao();
    }

    private void mostrarErro(String mensagem) {
        carregandoQuestoes = false;

        if (progressBarSimulado != null) {
            progressBarSimulado.setVisibility(View.GONE);
        }

        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();

        if (!questoes.isEmpty()) {
            mostrarQuestao();
        } else {
            finish();
        }
    }

    private void mostrarQuestao() {
        try {
            if (questoes == null || questaoAtual >= questoes.size()) {
                // Show final results instead of just finishing
                mostrarResultadoFinal();
                return;
            }

            Questao questao = questoes.get(questaoAtual);

            textTitulo.setText(questao.getTitle());
            txtDisciplina.setText(questao.getDiscipline());
            txtIntroAlternativas.setText(questao.getAlternativesIntroduction() + ":");

            final Markwon markwon = Markwon.create(this);

            String contextoStr = questao.getContext();

            if (contextoStr.startsWith("![](")) {
                textContexto.setText("");
            } else {
                markwon.setMarkdown(textContexto, questao.getContext());
            }

            radioGroup.removeAllViews();

            for (Alternativa alternativa : questao.getAlternatives()) {
                RadioButton rb = new RadioButton(this);
                rb.setText(alternativa.getText());
                rb.setTag(alternativa.getLetter());
                radioGroup.addView(rb);
            }

            if (questao.getFiles() != null && !questao.getFiles().isEmpty()) {
                Glide.with(this).load(questao.getFiles().get(0)).into(imageView);
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }

            // Update button text for last question
            if (questaoAtual == questoes.size() - 1) {
                buttonResponder.setText("Finalizar Simulado");
            } else {
                buttonResponder.setText("Próxima");
            }

        } catch (Exception e) {
            Log.e("MOSTRAR_QUESTAO", "Erro ao exibir questão", e);
        }
    }
    private void verificarResposta() {
        try {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Escolha uma alternativa.", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selecionado = findViewById(selectedId);
            if (selecionado == null) {
                Toast.makeText(this, "Erro interno: alternativa inválida.", Toast.LENGTH_SHORT).show();
                return;
            }

            Object tag = selecionado.getTag();
            if (!(tag instanceof String)) {
                Toast.makeText(this, "Erro interno: tag inválida na alternativa.", Toast.LENGTH_SHORT).show();
                return;
            }

            String letraSelecionada = (String) tag;

            if (questaoAtual >= questoes.size()) {
                Toast.makeText(this, "Erro: questão fora do intervalo.", Toast.LENGTH_SHORT).show();
                return;
            }

            Questao questao = questoes.get(questaoAtual);
            String respostaCorreta = questao.getCorrectAlternative();

            respostasUsuario.add(letraSelecionada);
            respostasCorretas.add(respostaCorreta);
            disciplinas.add(questao.getDiscipline());

            if (letraSelecionada.equalsIgnoreCase(respostaCorreta)) {
                acertos++;
            }

            questaoAtual++;
            mostrarQuestao();

        } catch (Exception e) {
            Log.e("VERIFICAR_RESPOSTA", "Erro ao verificar resposta", e);
            Toast.makeText(this, "Erro ao verificar resposta: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


//    private void verificarResposta() {
//        try {
//            int selectedId = radioGroup.getCheckedRadioButtonId();
//            if (selectedId == -1) {
//                Toast.makeText(this, "Escolha uma alternativa.", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            RadioButton selecionado = findViewById(selectedId);
//            String letraSelecionada = (String) selecionado.getTag();
//
//            Questao questao = questoes.get(questaoAtual);
//            String respostaCorreta = questao.getCorrectAlternative();
//
//            // Store the answers for final review
//            respostasUsuario.add(letraSelecionada);
//            respostasCorretas.add(respostaCorreta);
//            disciplinas.add(questao.getDiscipline());
//
//            // Check if answer is correct (no immediate feedback)
//            if (letraSelecionada.equalsIgnoreCase(respostaCorreta)) {
//                acertos++;
//            }
//
//            questaoAtual++;
//            mostrarQuestao();
//        } catch (Exception e) {
//            Log.e("VERIFICAR_RESPOSTA", "Erro ao verificar resposta", e);
//            Toast.makeText(this, "Erro ao verificar resposta: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }

    private void mostrarResultadoFinal() {
        // Start ResultsActivity with all the data
        Intent intent = new Intent(this, Resultados.class);
        intent.putExtra("acertos", acertos);
        intent.putExtra("total", questoes.size());
        intent.putExtra("ano", anoEscolhido);
        intent.putStringArrayListExtra("respostasUsuario", new ArrayList<>(respostasUsuario));
        intent.putStringArrayListExtra("respostasCorretas", new ArrayList<>(respostasCorretas));
        intent.putStringArrayListExtra("disciplinas", new ArrayList<>(disciplinas));

        startActivity(intent);
        finish();
    }

    public int sortearAnoProva() {
        return random.nextInt(anosProvasDisponiveis.length);
    }
}