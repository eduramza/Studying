package com.ramattecgmail.rafah.studying.Utils;

import android.util.Base64;

import java.util.Random;

/**
 * Created by rafah on 02/09/2017.
 */

public class CriptografiaBase64 {

    //METODO DE CODIFICAÇÃO E DECODIFICAÇÃO
    public static String criptografarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "perfil");
    }

    public static String descriptografarBase64(String texto){
        return new String(Base64.decode(texto, Base64.DEFAULT));
    }

    public static String criptografarBase64Atividade(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "atividade");
    }

}
