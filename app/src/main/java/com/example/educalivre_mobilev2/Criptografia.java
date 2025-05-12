package com.example.educalivre_mobilev2;

import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

public class Criptografia {

    // Gera o hash da senha
    public static String gerarHash(String senha) {
        try {
            return BCrypt.hashpw(senha, BCrypt.gensalt());
        } catch (Exception e) {
            Log.e("Criptografia", "Erro ao gerar hash: " + e.getMessage(), e);
            return null;
        }
    }

    public static boolean verificarSenha(String senhaDigitada, String hashArmazenado) {
        try {
            return BCrypt.checkpw(senhaDigitada, hashArmazenado);
        } catch (Exception e) {
            Log.e("Criptografia", "Erro ao verificar senha: " + e.getMessage(), e);
            return false;
        }
    }

}

