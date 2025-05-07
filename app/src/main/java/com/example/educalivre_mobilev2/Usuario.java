package com.example.educalivre_mobilev2;

public class Usuario {
    private static int id;
    private static String nome,sobrenome, email, celular, genero, senha, dataCriacao;

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        Usuario.id = id;
    }

    public static String getSobrenome() {
        return sobrenome;
    }

    public static void setSobrenome(String sobrenome) {
        Usuario.sobrenome = sobrenome;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Usuario.email = email;
    }

    public static String getCelular() {
        return celular;
    }

    public static void setCelular(String celular) {
        Usuario.celular = celular;
    }

    public static String getGenero() {
        return genero;
    }

    public static void setGenero(String genero) {
        Usuario.genero = genero;
    }

    public static String getSenha() {
        return senha;
    }

    public static void setSenha(String senha) {
        Usuario.senha = senha;
    }

    public static String getDataCriacao() {
        return dataCriacao;
    }

    public static void setDataCriacao(String dataCriacao) {
        Usuario.dataCriacao = dataCriacao;
    }

    public static String getNome() {
        return nome;
    }

    public static void setNome(String nome) {
        Usuario.nome = nome;
    }
}
