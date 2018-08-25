package com.ramattecgmail.rafah.studying.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.ramattecgmail.rafah.studying.Adapters.MinhasAtividadesAdapter;
import com.ramattecgmail.rafah.studying.Config.ConfiguracaoFirebase;
import com.ramattecgmail.rafah.studying.Models.Atividade;
import com.ramattecgmail.rafah.studying.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaAtividadesFragment extends Fragment implements SearchView.OnQueryTextListener{
    //ATRIBUTOS
    SearchView searchView;
    ListView listView;
    ArrayAdapter atividadeArrayAdapter;
    ArrayList<Atividade> atividadeArrayList;
    private DatabaseReference reference;
    private ChildEventListener childEventListener;

    String idUsuario;

    public ListaAtividadesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_lista_atividades, container, false);

        searchView = (SearchView) view.findViewById(R.id.sv_listaAtiv);
        listView = (ListView) view.findViewById(R.id.lv_atividades);

        //Montando ListView e Adapter
        atividadeArrayList = new ArrayList<>();
        atividadeArrayAdapter = new MinhasAtividadesAdapter(getActivity(), atividadeArrayList);
        listView.setAdapter(atividadeArrayAdapter);

        searchView.setOnQueryTextListener(this);

        /*********************** RECUPERANDO A LISTA DE ATIVIDADES SEM FILTRO AINDA ***********************/
        reference = ConfiguracaoFirebase.getReferenceFirebase()
                .child("ATIVIDADES");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                atividadeArrayList.clear();

                //Vinculando cada item a lista
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    //passando cada valor dentro da classe modelo
                    Atividade atividade = data.getValue(Atividade.class);
                    atividadeArrayList.add(atividade);
                }

                atividadeArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                atividadeArrayList.clear();

                //Vinculando cada item a lista
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    //passando cada valor dentro da classe modelo
                    Atividade atividade = data.getValue(Atividade.class);
                    atividadeArrayList.add(atividade);
                }

                atividadeArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        return view;
    }

    /*************************** METODOS *******************************/
    @Override
    public void onStart() {
        super.onStart();
        //reference.addValueEventListener(valueEventListener);
        reference.addChildEventListener(childEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        //reference.removeEventListener(valueEventListener);
        reference.removeEventListener(childEventListener);
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        atividadeArrayAdapter.getFilter().filter(newText);
        return false;
    }
}
