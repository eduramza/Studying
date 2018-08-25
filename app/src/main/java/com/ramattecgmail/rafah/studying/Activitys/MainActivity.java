package com.ramattecgmail.rafah.studying.Activitys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.ramattecgmail.rafah.studying.Config.ConfiguracaoFirebase;
import com.ramattecgmail.rafah.studying.Fragments.AtividadeFragment;
import com.ramattecgmail.rafah.studying.Fragments.ConfiguracoesFragment;
import com.ramattecgmail.rafah.studying.Fragments.AtividadesCriadasFragment;
import com.ramattecgmail.rafah.studying.Fragments.ListaAtividadesFragment;
import com.ramattecgmail.rafah.studying.Fragments.PerfilFragment;
import com.ramattecgmail.rafah.studying.Models.Usuarios;
import com.ramattecgmail.rafah.studying.R;
import com.ramattecgmail.rafah.studying.Utils.SharedPreferencesUser;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    private NavigationView navView;
    private View header;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView nome, email;
    private RoundedImageView imPerfil;
    private String idUsuario;
    private String idFotoPerfil;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializando a toolbar e suas configurações
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Inicializando NavigationView
        navView = (NavigationView) findViewById(R.id.nv_main);
        header = navView.getHeaderView(0);
        nome = (TextView) header.findViewById(R.id.tv_nome_header);
        email = (TextView) header.findViewById(R.id.tv_email_header);
        imPerfil = (RoundedImageView) header.findViewById(R.id.img_perfil_header);

        //*********************** INICIO RECUPERAÇÃO DOS DADOS DO USUÁRIO LOGADO *************************/

        SharedPreferencesUser preferencesUser = new SharedPreferencesUser(this);
        idUsuario = preferencesUser.getIdentificador();

        reference = ConfiguracaoFirebase.getReferenceFirebase()
                .child("USUARIOS")
                .child(idUsuario);

        /*********************** INICIO CONFIGURAÇÃO PARA O STORAGE ***************************/

        idFotoPerfil = idUsuario + "-perfil";
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        storageReference = storage.getReferenceFromUrl("gs://assistente-de-estudo.appspot.com")
                .child(idFotoPerfil);

        /************************ FIM CONFIGURAÇÃO PARA O STORAGE ******************************/

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Usuarios usuario = dataSnapshot.getValue(Usuarios.class);

                    //SETANDO OS VALORES NA CLASSE MODEL
                    nome.setText(usuario.getNome());
                    email.setText(usuario.getEmail());

                    //recuperando a foto
                    usuario.recuperarFotoPerfil(MainActivity.this.getApplicationContext(), imPerfil, idFotoPerfil, true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(valueEventListener);
        //*********************** FIM RECUPERAÇÃO DOS DADOS DO USUÁRIO LOGADO *************************/

        //Configurando navgationView e a ação do item selecionado
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            //Esse método é chamado quando um item é selecionado
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Checando se um item tem o estado de checked ou não
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                //Fechando o drawer no evento de click
                drawerLayout.closeDrawers();

                return menuItemSelecionado(item.getItemId());

            }
        });

        //Inicializando o drawer layout e o toogleBar
        drawerLayout = (DrawerLayout) findViewById(R.id.dl_main);
        ActionBarDrawerToggle actionBarDrawer = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        //Configurando a ação de alternancia para o drawer layout
        //O estado da sincronização é necessário caso contrario problemas com a chamada de icones ocorrerão
        actionBarDrawer.syncState();

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //google api client settings
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize the Branch object
        Branch.getAutoInstance(this);
    }

    /************************** METODOS ******************************/

    /********************Códigos para chamadas externas do APP com Branch.io***************************/
    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    // ... insert custom logic here ...
                } else {
                    Log.i("MyApp", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }
    /******************FIM - Códigos para chamadas externas do APP com Branch.io*************************/

    @Override
    protected void onResume() {
        super.onResume();
        reference.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        reference.removeEventListener(valueEventListener);
    }


    private void signOut(){
        //firebase
        mAuth.signOut();

        //Google SignOut
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                openLoginActivity();
                Toast.makeText(MainActivity.this, "Deslogado com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //METODO RESPONSAVEL PELA AÇÃO DE CADA ITEM DE MENU NO NAVIGATIONVIEW
    private boolean menuItemSelecionado(int item){

        //Configurando o evento de transição entre as activities
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        //Verificando a ação a ser tomada para cada item clicado no menu de navgação
        switch (item){
            case R.id.gi_perfil:
                PerfilFragment perfilFragment = new PerfilFragment();
                fragmentTransaction.replace(R.id.frame, perfilFragment);
                fragmentTransaction.commit();
                return true;

            case R.id.gi_abrirA:
                AtividadeFragment atividade = new AtividadeFragment();
                fragmentTransaction.replace(R.id.frame, atividade);
                fragmentTransaction.commit();
                return true;

            case R.id.gi_consultarA:
                //AtividadesCriadasFragment consulta = new AtividadesCriadasFragment();
                ListaAtividadesFragment consulta = new ListaAtividadesFragment();
                fragmentTransaction.replace(R.id.frame, consulta);
                fragmentTransaction.commit();
                return true;

            case R.id.gi_avaliar:
                Toast.makeText(this, "Clicado em Avaliar", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.gi_email:
                Toast.makeText(this, "Clicado em e-mail", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.gi_config:
                ConfiguracoesFragment configuracoes = new ConfiguracoesFragment();
                fragmentTransaction.replace(R.id.frame, configuracoes);
                fragmentTransaction.commit();
                return true;

            case R.id.gi_sair:
                signOut();
                return true;
        }
        return true;
    }

    private void openLoginActivity(){
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
