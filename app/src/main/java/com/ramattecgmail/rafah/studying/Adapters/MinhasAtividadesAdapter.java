package com.ramattecgmail.rafah.studying.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.ramattecgmail.rafah.studying.Models.Atividade;
import com.ramattecgmail.rafah.studying.R;

import java.util.ArrayList;

/**
 * Created by rafah on 22/09/2017.
 */

public class MinhasAtividadesAdapter extends ArrayAdapter<Atividade>{
    //ATRIBUTOS
    private ArrayList<Atividade> atividadeArrayList;
    private Context context;

    public MinhasAtividadesAdapter(@NonNull Context c, @NonNull ArrayList<Atividade> objects) {
        super(c, 0, objects);

        this.atividadeArrayList = objects;
        this.context = c;
    }

    //METODO PARA EXIBIÇÃO DA LISTA


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Criando uma view do zero
        View view = null;

        //Validando a lista
        if (atividadeArrayList != null){
            //Inicializando o objeto para montagem das views
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //Montando a view a partir do XML
            view = inflater.inflate(R.layout.lista_atividades, parent, false);

            //Recuperando os elementos para exibição
            TextView id = (TextView) view.findViewById(R.id.tvID_list);
            TextView titulo = (TextView) view.findViewById(R.id.tvTitulo_list);
            TextView tempo = (TextView) view.findViewById(R.id.tvTempo_list);
            RoundedImageView imCategoria = (RoundedImageView) view.findViewById(R.id.im_categoria_list);

            Atividade atividade = atividadeArrayList.get(position);
            id.setText(atividade.getId());
            titulo.setText(atividade.getTitulo());
            tempo.setText(atividade.getTempo());

        }
        return view;
    }
}
