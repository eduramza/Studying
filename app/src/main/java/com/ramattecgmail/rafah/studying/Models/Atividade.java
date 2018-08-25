package com.ramattecgmail.rafah.studying.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ramattecgmail.rafah.studying.Config.ConfiguracaoFirebase;

/**
 * Created by rafah on 20/09/2017.
 */

public class Atividade {
    //ATRIBUTOS
    private String id, categoria, tempo, titulo, descricao, idUsuario, atendimentos, nivel, data_Abertura, Data_Encerramento,
                    comentarios, anexos, likes, dislikes, tempoTotal, linkFilho;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    //Construtor padrão
    public Atividade(){

    }

    public void salvarAtividade(){
        //salvando os dados no realtime database
        DatabaseReference bancoAtividades = ConfiguracaoFirebase.getReferenceFirebase();
        bancoAtividades.child("ATIVIDADES").child(getIdUsuario()).child(getId()).setValue(this);//Salvando todos os dados armazenados nessa classe
    }

    /**************** METODOS DO FIREBASE ************************/


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getAtendimentos() {
        return atendimentos;
    }

    public void setAtendimentos(String atendimentos) {
        this.atendimentos = atendimentos;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getData_Abertura() {
        return data_Abertura;
    }

    public void setData_Abertura(String data_Abertura) {
        this.data_Abertura = data_Abertura;
    }

    public String getData_Encerramento() {
        return Data_Encerramento;
    }

    public void setData_Encerramento(String data_Encerramento) {
        Data_Encerramento = data_Encerramento;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public String getAnexos() {
        return anexos;
    }

    public void setAnexos(String anexos) {
        this.anexos = anexos;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDislikes() {
        return dislikes;
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
    }

    public String getTempoTotal() {
        return tempoTotal;
    }

    public void setTempoTotal(String tempoTotal) {
        this.tempoTotal = tempoTotal;
    }

    public String getLinkFilho() {
        return linkFilho;
    }

    public void setLinkFilho(String linkFilho) {
        this.linkFilho = linkFilho;
    }
}
