package com.ramattecgmail.rafah.studying.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ramattecgmail.rafah.studying.Activitys.MainActivity;
import com.ramattecgmail.rafah.studying.Activitys.SignInActivity;
import com.ramattecgmail.rafah.studying.Config.ConfiguracaoFirebase;
import com.ramattecgmail.rafah.studying.Models.Usuarios;
import com.ramattecgmail.rafah.studying.R;
import com.ramattecgmail.rafah.studying.Utils.SharedPreferencesUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class PerfilFragment extends Fragment {
    //ATRIBUTOS
    private static final int RC_IN_GALERY = 1;
    String idUsuario, idFotoPerfil;
    private TextView nome, email, tel, nasc, prof, pais, cid, estado, escolaridade;
    private Button editar;
    private ImageView imPerfil, imGallery;

    private DatabaseReference firebase;
    private ValueEventListener valueEventListener;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    byte[] data;

public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //INTANCIANDO OS COMPONENTES
        nome = view.findViewById(R.id.tv_nomePerfil);
        email = view.findViewById(R.id.tv_emailPerfil);
        tel = view.findViewById(R.id.tv_telPerfil);
        nasc = view.findViewById(R.id.tv_nascPerfil);
        pais = view.findViewById(R.id.tv_paisPerfil);
        prof = view.findViewById(R.id.tv_profPerfil);
        cid = view.findViewById(R.id.tv_cidadePerfil);
        estado = view.findViewById(R.id.tv_estadoPerfil);
        escolaridade = view.findViewById(R.id.tv_escolPerfil);
        editar = view.findViewById(R.id.bt_editarPerfil);
        imPerfil = view.findViewById(R.id.img_perfilPerfil);
        imGallery = view.findViewById(R.id.im_galeriaPerfil);

        /********************INICIO DA CAPTURA DE DADOS DO USUARIO ****************************/
        SharedPreferencesUser preferencesUser = new SharedPreferencesUser(getActivity());
        idUsuario = preferencesUser.getIdentificador();

        firebase = ConfiguracaoFirebase.getReferenceFirebase()
                .child("USUARIOS")
                .child(idUsuario);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    Usuarios usuario = dataSnapshot.getValue(Usuarios.class);

                    //SETANDO OS VALORES NA CLASSE MODEL
                    nome.setText(usuario.getNome());
                    email.setText(usuario.getEmail());
                    nasc.setText(usuario.getNascimento());
                    prof.setText(usuario.getProfissao());
                    pais.setText(usuario.getPais());
                    estado.setText(usuario.getEstado());
                    cid.setText(usuario.getCidade());
                    tel.setText(usuario.getTelefone());
                    escolaridade.setText(usuario.getEscolaridade());

                    usuario.recuperarFotoPerfil(getContext(), imPerfil, idFotoPerfil, false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        /**********************FIM DA CAPTURA DE DADOS DO USUARIO *****************************/

        /*********************** INICIO CONFIGURAÇÃO PARA O STORAGE ***************************/

        idFotoPerfil = idUsuario + "-perfil";
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        storageReference = storage.getReferenceFromUrl("gs://assistente-de-estudo.appspot.com")
                .child(idFotoPerfil);

        /************************ FIM CONFIGURAÇÃO PARA O STORAGE ******************************/
        firebase.addValueEventListener(valueEventListener);

        //EVENTOS DE CLICK
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            }
        });

        imGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        return view;
    }

    private void openGallery(){
        //ABRINDO A GALERIA DE IMAGENS, passando uma ação para a intent para localizar a imagem, ACTION_PICK para escolher uma imagem
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galeria, RC_IN_GALERY);
    }

    //METODO QUE TRATA O RETORNO DAS ACTIVITYS
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Testando o processo de retorno dos dados
        if (requestCode == RC_IN_GALERY && resultCode == RESULT_OK && data != null){
            //recuperar o local do recurso
            Uri localImagem = data.getData();
            //recuerando a imagem do local que ela foi selecionada
            try {
                Bitmap imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagem);
                //Comprimir a imagem recuperada em formato PNG, PNG ignora qualidade e sempre é o max
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.PNG, 80, stream);

                //Conversão em bytes para upload
                byte[] byteArray = stream.toByteArray();

                //Fazendo upload
                uploadFoto(byteArray);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadFoto(byte[] byteFoto){
        //O CÓDIGO ABAIXO FAZ O COMPARTILHAMNTO DE UMA FOTO EM CONUNTO COM A CONFIGURAÇÃO PARA STORAGE
        UploadTask uploadTask = storageReference.putBytes(byteFoto);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Não foi possivel fazer upload de foto", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Upload Executado com sucesso!", Toast.LENGTH_SHORT).show();
                //Atualizando os dados do usuário
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null){
                            Usuarios usuario = dataSnapshot.getValue(Usuarios.class);
                            usuario.setFotoPerfil(idFotoPerfil);
                            usuario.atualizar();

                            //Atualizando a foto na tela
                            usuario.recuperarFotoPerfil(getContext(), imPerfil, idFotoPerfil, false);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
