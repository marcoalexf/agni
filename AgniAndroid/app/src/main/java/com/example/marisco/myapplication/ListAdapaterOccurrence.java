package com.example.marisco.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListAdapaterOccurrence extends BaseAdapter {

    private ArrayList<Object> mData;

    public ListAdapaterOccurrence(ArrayList<Object> list) {
        mData = new ArrayList<>();
        mData.addAll(list);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
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

        OccurrenceData o = new Gson().fromJson(getItem(i).toString(), OccurrenceData.class);

        ((TextView) result.findViewById(R.id.occurence_title)).setText(o.getTitle());
        ((TextView) result.findViewById(R.id.occurence_description)).setText(o.getDescription());

        return result;
    }
}
