package com.example.educalivre_mobilev2;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;

public class Acessa {
    ResultSet RS;
    java.sql.Statement stmt;
    Connection con;

    public Connection entBanco(Context ctx) {
        try {
            // Permitir conexão em thread principal (não recomendado para produção)
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Carrega o driver
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
           // Toast.makeText(ctx.getApplicationContext(), "Driver carregado com sucesso", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            Toast.makeText(ctx.getApplicationContext(), "Driver não encontrado", Toast.LENGTH_SHORT).show();
            Log.e("Conexao", "Classe do driver não encontrada", e);
            return null;
        } catch (InstantiationException | IllegalAccessException e) {
            Toast.makeText(ctx.getApplicationContext(), "Falha ao instanciar o driver", Toast.LENGTH_SHORT).show();
            Log.e("Conexao", "Erro ao instanciar o driver", e);
            return null;
        }

        try {
            // Dados da conexão
            String url = "jdbc:jtds:sqlserver://192.168.1.5;databaseName=educaLivre";
            String user = "sa";
            String pass = "123";

            // Estabelece conexão
            con = DriverManager.getConnection(url, user, pass);
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
           // Toast.makeText(ctx.getApplicationContext(), "Conectado ao banco de dados", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            //Toast.makeText(ctx.getApplicationContext(), "Erro ao conectar ao banco de dados", Toast.LENGTH_SHORT).show();
            Log.e("Conexao", "Erro de SQL ao conectar", e);
            return null;
        }

        return con;
    }

}
