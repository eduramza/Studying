package com.ramattecgmail.rafah.studying.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rafah on 02/09/2017.
 * SALVANDO OS DADOS TEMPORÁRIOS DO USUÁRIO PARA SEREM ACESSADOS APÓS A TELA DE LOGIN
 */

public class SharedPreferencesUser {

    //ATRIBUTOS
    private Context contexto;
    private SharedPreferences preferencias;
    private final String NOME_ARQUIVO = "STUDYNG.PREFERENCES";
    private final int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_IDENTIFICADOR = "identificador";
    private final String CHAVE_NOME = "nomeUsuario";

    public SharedPreferencesUser(Context contextoParametro){
        contexto = contextoParametro;
        preferencias = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferencias.edit();
    }

    //SALVANDO E RECUPERANDO USUARIOS
    public void salvarUsuarioPreferences(String ident, String nome){
        editor.putString(CHAVE_IDENTIFICADOR, ident);
        editor.putString(CHAVE_NOME, nome);
        editor.commit();
    }

    //RECUPERANDO
    public String getIdentificador(){
        return preferencias.getString(CHAVE_IDENTIFICADOR, null);
    }

    public String getUsuarioNome(){
        return preferencias.getString(CHAVE_NOME, null);
    }
}
