package com.ramattecgmail.rafah.studying.Config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by rafah on 01/09/2017.
 */

public final class ConfiguracaoFirebase {
    //ATRIBUTOS
    private static DatabaseReference referenceFirebase;
    private static FirebaseAuth autenticacao;
    private static FirebaseStorage firebaseStorage;
    private static FirebaseDatabase firebaseDatabase;
    private static StorageReference storageReferenceAtividade;
    //StorageReference anexosRef;

    //Fazendo a conexão com o banco em tempo real
    public static DatabaseReference getReferenceFirebase(){
        //verificando se já possui a referencia do banco
        if (referenceFirebase == null){
            referenceFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenceFirebase;
    }

    //Fazendo validação de autenticação e login
    public static FirebaseAuth getFirebaseAutenticacao(){
        if (autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }

        return autenticacao;
    }

    //Fazendo validação para armazenamento de imagens
    public static FirebaseStorage getFirebaseStorage(){
        if (firebaseStorage == null){
            firebaseStorage = FirebaseStorage.getInstance();
        }
        return firebaseStorage;
    }

    //Fazendo referencia para armazenamento de anexos de atividades
    public static StorageReference getStorageAttachments(){
        if (storageReferenceAtividade == null){
            storageReferenceAtividade = firebaseStorage.getInstance().getReference().child("ATIVIDADES_ANEXOS");
            //StorageReference anexosRef = storageReferenceAtividade.child("ATIVIDADES_ANEXOS");
        }
        return storageReferenceAtividade;
    }

    public static FirebaseDatabase getFirebaseDatabase(){
        if (firebaseDatabase == null){
            firebaseDatabase = FirebaseDatabase.getInstance();
        }
        return firebaseDatabase;
    }

}
