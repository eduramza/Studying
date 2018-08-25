package com.ramattecgmail.rafah.studying.Activitys;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.aware.PublishConfig;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ramattecgmail.rafah.studying.Config.ConfiguracaoFirebase;
import com.ramattecgmail.rafah.studying.Models.Usuarios;
import com.ramattecgmail.rafah.studying.R;
import com.ramattecgmail.rafah.studying.Utils.CriptografiaBase64;
import com.ramattecgmail.rafah.studying.Utils.SharedPreferencesUser;


/***********
 * created by: EDUARDO BRANDÃO
 * creation date: 27/08/2017
 * porpouse: Classe responsavel por efetuar o login do usuário no App
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener{

    //ATTRIBUTES
    private static final int RC_SIGN_IN = 1;

    EditText etUsename, etPassword;
    private Toolbar toolbar;
    private String  gEmail, gNome, gID;

    private FirebaseAuth authentication;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient mGoogleApiClient;

    private DatabaseReference firebase;
    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Checking logged in user
        logonStarter();

        //Inicializando a toolbar e suas configurações
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //instantiating the components
        etUsename = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.bt_google).setOnClickListener(this);

        //INITIALIZING THE GOOGLE LOGIN SETTINGS
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //END INITIALIZING THE GOOGLE LOGIN SETTINGS
        //google api client settings
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //OUVINTE DA TABELA USUARIO

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sNasc = (String) dataSnapshot.getValue();

                //SE O NASCIMENTO ESTIVER VAZIO SIGNIFICA QUE ELE AINDA NÃO TEM CADASTRO
                if (sNasc == null) {
                    //Cadastrar o usuário no firebase
                    Usuarios usuario = new Usuarios();
                    usuario.setEmail(gEmail);
                    usuario.setNome(gNome);
                    String identificador = CriptografiaBase64.criptografarBase64(gID);
                    usuario.setId(identificador);
                    usuario.salvar();

                    openCadastroActivity();
                } else {
                    openMainActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    /************************* METHODS ****************************************/
    private void logonStarter(){
        authentication = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if (authentication.getCurrentUser() != null){
            openMainActivity();
        }
    }

    //chamada da activity de seleção de conta do google
    private void GoogleSignIn(){
        Intent signinIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signinIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Testing result
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                //Google Sigin as succesfull, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct){
        Toast.makeText(this,"Id: " +acct.getId() + " ServerAuth: "
                + acct.getServerAuthCode() + " Account: " +acct.getAccount(), Toast.LENGTH_SHORT).show();
        //Family: Brandão, Display Eduardo Brandão, Given Eduardo

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        authentication.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //se o a autenticação com o fire base for bem sucedida
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Firebase Sucesso Também!", Toast.LENGTH_SHORT).show();
                            //Cadastrar o usuário no firebase
                            gEmail = acct.getEmail();
                            gNome = acct.getDisplayName();
                            gID = acct.getId();
                           /*Usuarios usuario = new Usuarios();
                            usuario.setEmail(acct.getEmail());
                            usuario.setNome(acct.getDisplayName());*/
                            String identificador = CriptografiaBase64.criptografarBase64(acct.getId());
                            //usuario.setId(identificador);
                            //usuario.salvar();

                            //PARA QUANDO UM NOVO USUÁRIO FOR AUTENTICADO NO SISTEMA
                            firebase = ConfiguracaoFirebase.getReferenceFirebase()
                                    .child("USUARIOS")
                                    .child(identificador)
                                    .child("nascimento");

                            //SALVANDO OS DADOS NO SHARED PREFERENCES
                            SharedPreferencesUser preferencesUser = new SharedPreferencesUser(LoginActivity.this);
                            preferencesUser.salvarUsuarioPreferences(identificador, gNome);

                            firebase.addValueEventListener(valueEventListener);
                            //openCadastroActivity();
                        } else {
                            Log.w("Login Result", "signInWithCredential:failure ", task.getException());
                        }

                    }
                });
    }

    private void openMainActivity(){
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);

    }

    private void openCadastroActivity(){
        Intent cadastro = new Intent(LoginActivity.this, SignInActivity.class);
        startActivity(cadastro);
    }

    //Buttons Listeners
    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (v.getId()){
            case R.id.bt_google:
                GoogleSignIn();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
