package com.example.educalivre_mobilev2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Cadastro extends AppCompatActivity {
    EditText nomeCadastro, sobrenomeCadastro, emailCadastro, celularCadastro, senhaCadastro, confirmeSenhaCadastro;
    RadioGroup radioGroup;
    RadioButton radioButtonMasculino, radioButtonFeminino, radioButtonOutro, radioButtonNenhum;
    Criptografia crp = new Criptografia();
    Acessa objA = new Acessa();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nomeCadastro = findViewById(R.id.nomeCadastro);
        sobrenomeCadastro = findViewById(R.id.sobrenomeCadastro);
        emailCadastro = findViewById(R.id.emailCadastro);
        celularCadastro = findViewById(R.id.celularCadastro);
        senhaCadastro = findViewById(R.id.senhaCadastro);
        confirmeSenhaCadastro = findViewById(R.id.confirmeSenhaCadastro);

        radioGroup = findViewById(R.id.genderGroup);
        radioButtonMasculino = findViewById(R.id.radioButton);
        radioButtonFeminino = findViewById(R.id.radioButton2);
        radioButtonOutro = findViewById(R.id.radioButton3);
        radioButtonNenhum = findViewById(R.id.radioButton4);
    }

    public void cadastrar(View V) {
        objA.entBanco(this);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dataCriacaoC = sdf.format(Calendar.getInstance().getTime());

        String nomeC = nomeCadastro.getText().toString();
        String sobrenomeC = sobrenomeCadastro.getText().toString();
        String emailC = emailCadastro.getText().toString();
        String celularC = celularCadastro.getText().toString();
        String senhaC = crp.gerarHash(senhaCadastro.getText().toString()); //senhaCadastro.getText().toString();
        String confirmeSenhaC = confirmeSenhaCadastro.getText().toString();


        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        String sexo = "";

        if (selectedRadioButtonId == radioButtonMasculino.getId()) {
            sexo = "M";
        } else if (selectedRadioButtonId == radioButtonFeminino.getId()) {
            sexo = "F";
        } else if (selectedRadioButtonId == radioButtonOutro.getId()) {
            sexo = "O";
        } else if (selectedRadioButtonId == radioButtonNenhum.getId()) {
            sexo = "N";
        }

        try {
            int res = objA.stmt.executeUpdate("INSERT INTO Usuario (nome, sobrenome, email, celular, genero, senha, dataCriacao)" +
                    " VALUES ('"+nomeC+"','"+sobrenomeC+"','"+emailC+"','"+celularC+"','"+sexo+"','"+senhaC+"','"+dataCriacaoC+"')");
            if(res != 0) {
                Toast.makeText(getApplicationContext(),"Cadastrado com Sucesso!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Cadastro.this, Login.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Dado Não Salvo", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex){
            ex.getStackTrace();
            Log.e("Erro: ", ex.getMessage());
        }
    }

    public void irParaLogin (View V) {
        Intent intent = new Intent(Cadastro.this, Login.class);
        startActivity(intent);
        finish();
    }
}