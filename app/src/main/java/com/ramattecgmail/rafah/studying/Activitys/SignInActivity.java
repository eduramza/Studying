package com.ramattecgmail.rafah.studying.Activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ramattecgmail.rafah.studying.Config.ConfiguracaoFirebase;
import com.ramattecgmail.rafah.studying.Models.Usuarios;
import com.ramattecgmail.rafah.studying.R;
import com.ramattecgmail.rafah.studying.Utils.SharedPreferencesUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SignInActivity extends AppCompatActivity {
    //ATRIBUTOS
    EditText nome, email, telefone, nascimento, escolaridade, profissao, pais, estado, cidade;
    Button cadastrar;
    TextInputLayout tiNome, tiEmail, tiTel, tiNasc, tiEscol, tiProf, tiPais, tiEst, tiCid;
    private String idUsuario;

    private DatabaseReference firebase;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //INSTANCIANDO OS COMPONENTES
        nome = (EditText) findViewById(R.id.et_nomeCad);
        tiNome = (TextInputLayout) findViewById(R.id.tiNomeCad);
        email = (EditText) findViewById(R.id.et_emailCad);
        tiEmail = (TextInputLayout) findViewById(R.id.tiEmailCad);
        telefone = (EditText) findViewById(R.id.et_telCad);
        tiTel = (TextInputLayout) findViewById(R.id.tiTelCad);
        nascimento = (EditText) findViewById(R.id.et_nascCad);
        tiNasc = (TextInputLayout) findViewById(R.id.tiNascCad);
        escolaridade = (EditText) findViewById(R.id.et_escolCad);
        tiEscol = (TextInputLayout) findViewById(R.id.tiescolCad);
        profissao = (EditText) findViewById(R.id.et_profCad);
        tiProf = (TextInputLayout) findViewById(R.id.tiProfCad);
        pais = (EditText) findViewById(R.id.et_paisCad);
        tiPais = (TextInputLayout) findViewById(R.id.tiPaisCad);
        estado = (EditText) findViewById(R.id.et_estadoCad);
        tiEst = (TextInputLayout) findViewById(R.id.tiEstadoCad);
        cidade = (EditText) findViewById(R.id.et_cidCad);
        tiCid = (TextInputLayout) findViewById(R.id.tiCidCad);
        cadastrar = (Button) findViewById(R.id.bt_cadastrar);

        //RECUPERANDO O ID DO USUARIO LOGADO NO SISTEMA
        SharedPreferencesUser preferencias = new SharedPreferencesUser(SignInActivity.this);
        idUsuario = preferencias.getIdentificador();

        //PEGANDO A REFERENCIA DO FIREBASE
        firebase = ConfiguracaoFirebase.getReferenceFirebase()
                .child("USUARIOS")
                .child(idUsuario);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarOUalterar();
            }
        });
    }

    /***********************************************METODOS****************************************************/
    @Override
    protected void onStart() {
        super.onStart();
        recuperarDados();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void cadastrarOUalterar() {
        //VERIFICANDO SE OS CAMPOS OBRIGATÓRIOS FORAM PREENCHIDOS
        int iNome = nome.getText().toString().trim().length();
        int iEmail = email.getText().toString().trim().length();
        int iNasc = nascimento.getText().toString().trim().length();
        int iProf = profissao.getText().toString().trim().length();
        int iEscola = escolaridade.getText().toString().trim().length();
        int iTel = telefone.getText().toString().trim().length();

        //validação da data
        String formato = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        String data = nascimento.getText().toString();

        try{
            Date date = sdf.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
            tiNasc.setError("Formato de data invalido!");
            return;
        }


        if (iNome == 0 || iEmail == 0 || iNasc == 0 || iProf == 0 || iEscola == 0 || iTel == 0) {
            if (iNome == 0) {
                tiNome.setError("Este campo é obrigatório!");
            }
            if (iEmail == 0) {
                tiEmail.setError("Este campo é obrigatório!");
            }
            if (iNasc == 0) {
                tiNasc.setError("Este campo é obrigatório!");
            }
            if (iProf == 0) {
                tiProf.setError("Este campo é obrigatório!");
            }
            if (iEscola == 0) {
                tiEscol.setError("Este campo é obrigatório!");
            }
            if (iTel == 0) {
                tiTel.setError("Este campo é obrigatório!");
            }
        }else {
            //SALVANDO OS DADOS PARA O USUÁRIO
            Usuarios usuario = new Usuarios();
            usuario.setNome(String.valueOf(nome.getText()));
            usuario.setEmail(String.valueOf(email.getText()));
            usuario.setTelefone(String.valueOf(telefone.getText()));
            usuario.setEscolaridade(String.valueOf(escolaridade.getText()));
            usuario.setPais(String.valueOf(pais.getText()));
            usuario.setCidade(String.valueOf(cidade.getText()));
            usuario.setEstado(String.valueOf(estado.getText()));
            usuario.setNascimento(String.valueOf(nascimento.getText()));
            usuario.setProfissao(String.valueOf(profissao.getText()));
            usuario.setId(idUsuario);
            usuario.atualizar();

            openMainActivity();
            finish();
            }
    } //FIM DO METODO DE CADASTRAR OU ALTERAR

    //TRAZER DADOS PARA ALTERAÇÃO
    private void recuperarDados(){

        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    //recuperando os dados na classe usuário
                    Usuarios usuLogado = dataSnapshot.getValue(Usuarios.class);

                    //SETANDO OS VALORES
                    nome.setText(usuLogado.getNome());
                    email.setText(usuLogado.getEmail());
                    telefone.setText(usuLogado.getTelefone());
                    estado.setText(usuLogado.getEstado());
                    escolaridade.setText(usuLogado.getEscolaridade());
                    pais.setText(usuLogado.getPais());
                    profissao.setText(usuLogado.getProfissao());
                    nascimento.setText(usuLogado.getNascimento());
                    cidade.setText(usuLogado.getCidade());

                } else {
                    Toast.makeText(SignInActivity.this, "Problemas ao recuperar dados", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignInActivity.this, "onCancelled Error Database - Eduh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMainActivity(){
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
