package com.example.marisco.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListAdapaterOccurrence extends BaseAdapter {

    private ArrayList<Map<String, Object>> mData;
    Context context;

    public ListAdapaterOccurrence(Context context, List<Map<String, Object>> list) {
       // super(context, -1, list);
        mData = new ArrayList<>();
        mData.addAll(list);
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map<String, Object> getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mData.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View result;

        if (view == null) {
            result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_occurrence_item, viewGroup, false);
        } else {
            result = view;
        }

        /* @FRANCISCO, LISTEN WELL PEASENT IM ABOUT TO DROP SOME MAD KNOWLEDGE ON YOU

            SO TENS DE COPIAR ISTO: ((TextView) result.findViewById(R.id.user_occurrence_title)).setText(getItem(i).get("user_occurrence_title").toString());
            PARA CADA CENA QUE QUERES QUE APARECA NO LIST VIEW
            DEPOIS VAIS AO adapter_occurrence_item.xml E ACRESCENTAS LA TEXTVIEW's PARA CADA MERDA QUE QUERES MOSTRAR CARALHO
            MUDAS ALI O R.id.xxxxxxxxxxxxxxxxxxxxx ACCORDINGLY SEBEM?
            ALI O get("xxxxxxxxxxxxxxxxxxxxxxxxxxxx") MUDAS PARA AS VARIAS MERDAS QUE O DANIEL DEU NOME, ESSE PAANDULA.
            FODASSE DEPOIS DESTRE BREAKTHROUGH VOU ANDAR NU EM CASA
            VEMONOS LOGO
            PEACE
            -marisco
         */

        Map<String, Object> temp = getItem(i);
        Log.d("user_occurrence_title", temp.get("user_occurrence_title").toString());
        ((TextView) result.findViewById(R.id.user_occurrence_title)).setText(getItem(i).get("user_occurrence_title").toString());
       // ((TextView) result.findViewById(R.id.occurence_description)).setText(o.getDescription());

        return result;
    }
}
