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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ramattecgmail.rafah.studying.Config.ConfiguracaoFirebase;
import com.ramattecgmail.rafah.studying.Models.Atividade;
import com.ramattecgmail.rafah.studying.R;
import com.ramattecgmail.rafah.studying.Utils.CriptografiaBase64;
import com.ramattecgmail.rafah.studying.Utils.SharedPreferencesUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

import io.branch.referral.Branch;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AtividadeFragment extends Fragment {
    //ATRIBUTOS
    private static final int RC_IN_ANEXO = 2;
    EditText etAbertura, etOwner, etTitulo, etDescricao, etAtualizacao, etCategoria;
    TextView tvAtendimentos;
    Spinner spTempo, spNivel;
    Button btSalvar, btAnexos;

    String[] tempo = {"00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30", "04:00"};
    String[] nivel = {"1", "2", "3"};

    String idUsuario, nomeUsuario, idAtividade, numAtividade, idAnexo;
    byte[] byteArray = null;

    DatabaseReference firebase;
    StorageReference storageReference;

    public AtividadeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_atividade, container, false);

        //INSTANCIANDO OS COMPONENTES
        etAbertura = (EditText) view.findViewById(R.id.et_data_abertura);
        etAtualizacao = (EditText) view.findViewById(R.id.et_data_atualizacao);
        etCategoria = (EditText) view.findViewById(R.id.et_categoria);
        etTitulo = (EditText) view.findViewById(R.id.et_titulo);
        etDescricao = (EditText) view.findViewById(R.id.et_descricao);
        tvAtendimentos = (TextView) view.findViewById(R.id.tv_atendimentos);
        etOwner = (EditText) view.findViewById(R.id.et_Owner);
        spNivel = (Spinner) view.findViewById(R.id.spn_nivel);
        spTempo = (Spinner) view.findViewById(R.id.spn_tempo);
        btSalvar = (Button) view.findViewById(R.id.bt_salvar_atividade);
        btAnexos = (Button) view.findViewById(R.id.bt_anexar_AtivFrag);

        /************************ INICIO PARAMETRIZAÇÃO DOS COMPONENETES **************************/
        //Adicionando data atual na abertura
        String dataCorrente = DateFormat.getDateInstance().format(new Date());
        etAbertura.setText(dataCorrente);
        etAtualizacao.setText(dataCorrente);

        //Configurando os spinners
        ArrayAdapter tempoAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, tempo);
        ArrayAdapter nivelAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, nivel);
        spTempo.setAdapter(tempoAdapter);
        spNivel.setAdapter(nivelAdapter);

        //Recuperando os dados do usuario logado
        SharedPreferencesUser user = new SharedPreferencesUser(getActivity());
        idUsuario = user.getIdentificador();
        nomeUsuario = user.getUsuarioNome();

        //Setando o nome do criador da atividade
        etOwner.setText(nomeUsuario);
        etOwner.setEnabled(false);

        /************************** FIM PARAMETRIZAÇÃO DOS COMPONENETES ***************************/

        /******************************* EVENTO DOS BOTÕES ******************************************/
        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarAtividade();
            }
        });

        btAnexos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFolder();
            }
        });
        /****************************FIM  EVENTO DOS BOTÕES ****************************************/

        return view;
    }

    /************************************** SESSÃO DOS METODOS **********************************/
    public void salvarAtividade(){
        //Gerando numero aleatório para ID da atividade
        Random aleatorio = new Random();
        int i = aleatorio.nextInt(10000);
        numAtividade = String.valueOf(i);

        //Adicionando criptografia
        idAtividade = CriptografiaBase64.criptografarBase64Atividade(numAtividade);

        //Pegando a referencia do banco
        firebase = ConfiguracaoFirebase.getReferenceFirebase()
                .child("ATIVIDADES")
                .child(idAtividade);

        //Salvando os dados para o usuario
        Atividade atividade = new Atividade();
        atividade.setId(numAtividade);
        atividade.setCategoria(String.valueOf(etCategoria.getText()));
        atividade.setAtendimentos(String.valueOf(tvAtendimentos.getText()));
        atividade.setData_Abertura(String.valueOf(etAbertura.getText()));
        atividade.setData_Encerramento(String.valueOf(etAtualizacao.getText()));
        atividade.setDescricao(String.valueOf(etDescricao.getText()));
        atividade.setTempo(String.valueOf(spTempo.getSelectedItem()));
        atividade.setNivel(String.valueOf(spNivel.getSelectedItem()));
        atividade.setIdUsuario(String.valueOf(etOwner.getText()));
        atividade.setTitulo(String.valueOf(etTitulo.getText()));
        atividade.setIdUsuario(idUsuario);
        atividade.salvarAtividade();

        Toast.makeText(getActivity(), "Salvo com suesso!", Toast.LENGTH_SHORT).show();
        etCategoria.setText("");
        etTitulo.setText("");
        etDescricao.setText("");

        //Testando validação para fazer upload de anexos
        if (byteArray != null){
            uploadAnexo(byteArray);
        }

        //Para compartilhar a atividade recem criada
        //compartilharAtividade();

    }

    public void openFolder(){
        Intent folder = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        folder.setType("*/*");
        folder.addCategory(Intent.CATEGORY_OPENABLE);
        if (folder.resolveActivity(getActivity().getPackageManager()) != null){
            startActivityForResult(folder, RC_IN_ANEXO);
        }
    }

    //METODO QUE TRATA O RETORNO DAS ACTIVITYS
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Testando o processo de retorno dos dados
        if (requestCode == RC_IN_ANEXO && resultCode == RESULT_OK && data != null){
            //recuperar o local do recurso
            Uri arquivo = data.getData();
            //recuerando a imagem do local que ela foi selecionada
            try {
                Bitmap imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), arquivo);
                //Comprimir a imagem recuperada em formato PNG, PNG ignora qualidade e sempre é o max
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.PNG, 80, stream);

                //Conversão em bytes para upload
                byteArray = stream.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Fazer upload de arquivos
    public void uploadAnexo(byte[] byteAnexo){

        /******************************** CONFIGURAÇÃO PARA O STORAGE *******************************/
        idAnexo = idAtividade + "-anexo";
        storageReference = ConfiguracaoFirebase.getStorageAttachments()
                .child(idAnexo);

        /***************************** FIM - CONFIGURAÇÃO PARA O STORAGE ****************************/

        UploadTask uploadTask = storageReference.putBytes(byteAnexo);

        //Validando resultado
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Não foi possivel fazer upload do arquivo", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Anexo enviado com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void compartilharAtividade(){

        Branch branch = Branch.getInstance();

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_SUBJECT, "Teste de compartilhamento APP");
        share.putExtra(Intent.EXTRA_TEXT, "O usuario "+ nomeUsuario + " criou a atividade "+ numAtividade +" Teste de Link: https://bnc.lt/YrbX");

        startActivity(Intent.createChooser(share, "Teste de compartilhamento"));

    }

}
