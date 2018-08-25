package com.ramattecgmail.rafah.studying.Models;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.ramattecgmail.rafah.studying.Config.ConfiguracaoFirebase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rafah on 01/09/2017.
 */

public class Usuarios {
    //ATRIBUTOS
    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String nascimento;
    private String escolaridade;
    private String profissao;
    private String pais;
    private String estado;
    private String cidade;
    private String fotoPerfil;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    public Usuarios() {

    }

    public void salvar(){
        //Salvando os dados em um databasereference
        DatabaseReference banco = ConfiguracaoFirebase.getReferenceFirebase();
        banco.child("USUARIOS").child( getId()).setValue(this); //salvando todos os dados que foram armazenados nessa classe
    }

    public void atualizar(){
        //CRIANDO A REFERENCIA AO DADO QUE SERÁ ATUALIZADO
        DatabaseReference reference = ConfiguracaoFirebase.getFirebaseDatabase().getReference()
                .child("USUARIOS").child(getId());

        //CRIANDO O HASHMAP QUE ARMAZENA OS VALORES
        Map<String, Object> map = new HashMap<>();
        map.put("nome", getNome());
        map.put("email", getEmail());
        map.put("telefone", getTelefone());
        map.put("nascimento", getNascimento());
        map.put("escolaridade", getEscolaridade());
        map.put("profissao", getProfissao());
        map.put("pais", getPais());
        map.put("estado", getEstado());
        map.put("cidade", getCidade());
        map.put("fotoPerfil", getFotoPerfil());

        //COMANDO QUE MANDA ATUALIZAÇÃO PARA O FIREBASE
        reference.updateChildren(map);

    }

    public void recuperarFotoPerfil(final Context context, final ImageView imPerfil,
                                    String idFotoPerfil, final boolean arredondar){
        //getFile se o usuario quiser ter acesso ao download da imagem
        //recuperando a imagem pela referencia
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        storageReference = storage.getReferenceFromUrl("gs://assistente-de-estudo.appspot.com/")
                .child(idFotoPerfil);

        //Recuperando ele em formato de arquivo
        try{
            final File arquivoLocal = File.createTempFile("imagemPerfil", "jpg");
            storageReference.getFile(arquivoLocal).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //Verificando se precisa arredondar a imagem
                    if (arredondar == true){
                        Transformation transformation = new RoundedTransformationBuilder()
                                .borderColor(Color.BLACK)
                                .borderWidth(1)
                                .cornerRadius(30)
                                .oval(false)
                                .build();

                        Picasso.with(context)
                                .load(arquivoLocal)
                                .fit()
                                .transform(transformation)
                                .into(imPerfil);
                    } else {
                        Picasso.with(context)
                                .load(arquivoLocal)
                                .into(imPerfil);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (storageReference.getFile(arquivoLocal) == null){
                        Toast.makeText(context, "Não foi possivel recuperar a foto, favor verificar " +
                                "o acesso a internet e tentar novamente!", Toast.LENGTH_LONG).show();
                        Log.i("Erro", e.getMessage());
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getNascimento() {
        return nascimento;
    }

    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }

    public String getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(String escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String imPerfil) {
        this.fotoPerfil = imPerfil;
    }
}
