package com.ramattecgmail.rafah.studying.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ramattecgmail.rafah.studying.Adapters.MinhasAtividadesAdapter;
import com.ramattecgmail.rafah.studying.Config.ConfiguracaoFirebase;
import com.ramattecgmail.rafah.studying.Models.Atividade;
import com.ramattecgmail.rafah.studying.R;
import com.ramattecgmail.rafah.studying.Utils.SharedPreferencesUser;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AtividadesCriadasFragment extends Fragment implements SearchView.OnQueryTextListener {
    //ATRIBUTOS
    SearchView searchView;
    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Atividade> atividadeArrayList;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;
    String idUsuario;

    public AtividadesCriadasFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_minhas_atividades, container, false);

        searchView = (SearchView) view.findViewById(R.id.id_search);

        //Montando list view e adapter
        atividadeArrayList = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.lv_atividades);

        adapter = new MinhasAtividadesAdapter(getActivity(), atividadeArrayList);
        listView.setAdapter(adapter);
        //ATÉ AQUI JÁ FOI CRIADO UMA LISTA QUE É APRESENTADA PARA O USUÁRIO

        //Implementação do searchView
        searchView.setOnQueryTextListener(this);

        //*************** RECUPERANDO A LISTA DE ATIVIDADES CRIADAS POR ESSE USUÁRIO ************/
        SharedPreferencesUser user = new SharedPreferencesUser(getActivity());
        idUsuario = user.getIdentificador();

        reference = ConfiguracaoFirebase.getReferenceFirebase()
                .child("ATIVIDADES")
                .child(idUsuario);

        //Evento de consulta
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //LIMPANDO A LISTA
                atividadeArrayList.clear();

                //Listando cada uma das atividades criadas pelo usuário
                for (DataSnapshot dados: dataSnapshot.getChildren()){ //recupera os filhos do nó principal
                    Atividade atividade = dados.getValue(Atividade.class);
                    atividadeArrayList.add(atividade);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        return view;
    }

    /***************************** METODOS **********************************/
    @Override
    public void onStart() {
        super.onStart();
        reference.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        reference.removeEventListener(valueEventListener);
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String texto) {

        //Atividade atividade = new Atividade();
        adapter.getFilter().filter(texto);
        return false;
    }

}
