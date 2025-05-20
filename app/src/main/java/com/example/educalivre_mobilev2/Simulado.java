package com.example.educalivre_mobilev2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Simulado extends AppCompatActivity {
    private TextView textTitulo, textContexto;
    private ImageView imageView;
    private RadioGroup radioGroup;
    private Button buttonResponder;

    private List<Questao> questoes;
    private int questaoAtual = 0;
    private int acertos = 0;

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

        carregarQuestoes();

        buttonResponder.setOnClickListener(view -> verificarResposta());
    }

    private void carregarQuestoes() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.enem.dev/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService api = retrofit.create(ApiService.class);
            Call<RespostaAPI> call = api.listarQuestoes();

            call.enqueue(new Callback<RespostaAPI>() {
                @Override
                public void onResponse(Call<RespostaAPI> call, Response<RespostaAPI> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            questoes = response.body().getQuestions();

                            if (questoes == null || questoes.isEmpty()) {
                                Log.e("API_RESPONSE", "Lista de questões vazia ou nula");
                                Toast.makeText(Simulado.this, "Nenhuma questão encontrada.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            mostrarQuestao();
                        } else {
                            String erro = "Código HTTP: " + response.code();
                            Log.e("API_RESPONSE", "Erro na resposta da API: " + erro);
                            Toast.makeText(Simulado.this, "Erro ao carregar questões. " + erro, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("API_RESPONSE", "Erro ao processar resposta", e);
                        Toast.makeText(Simulado.this, "Erro ao processar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<RespostaAPI> call, Throwable t) {
                    Log.e("API_RESPONSE", "Falha ao conectar com a API", t);
                    Toast.makeText(Simulado.this, "Falha de conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e("API_INIT", "Erro ao inicializar Retrofit", e);
            Toast.makeText(Simulado.this, "Erro ao inicializar requisição: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarQuestao() {
        try {
            if (questoes == null || questaoAtual >= questoes.size()) {
                Toast.makeText(this, "Fim do simulado. Acertos: " + acertos + "/" + (questoes != null ? questoes.size() : 0), Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            Questao questao = questoes.get(questaoAtual);

            textTitulo.setText(questao.getTitle());
            textContexto.setText(questao.getContext());

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
        } catch (Exception e) {
            Log.e("MOSTRAR_QUESTAO", "Erro ao exibir questão", e);
            Toast.makeText(this, "Erro ao exibir questão: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            String letraSelecionada = (String) selecionado.getTag();

            Questao questao = questoes.get(questaoAtual);

            if (letraSelecionada.equalsIgnoreCase(questao.getCorrectAlternative())) {
                acertos++;
                Toast.makeText(this, "Correto!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Errado! Resposta certa: " + questao.getCorrectAlternative(), Toast.LENGTH_SHORT).show();
            }

            questaoAtual++;
            mostrarQuestao();
        } catch (Exception e) {
            Log.e("VERIFICAR_RESPOSTA", "Erro ao verificar resposta", e);
            Toast.makeText(this, "Erro ao verificar resposta: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
