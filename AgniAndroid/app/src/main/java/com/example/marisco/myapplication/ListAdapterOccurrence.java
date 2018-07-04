package com.example.marisco.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListAdapterOccurrence extends BaseAdapter {

    private ArrayList<Map<String, Object>> mData;
    Context context;

    public ListAdapterOccurrence(Context context, List<Map<String, Object>> list) {
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

        Map<String, Object> temp = getItem(i);

        Log.d("user_occurrence_title", temp.get("user_occurrence_title").toString());
        ((TextView) result.findViewById(R.id.occurrence_title)).setText(getItem(i).get("user_occurrence_title").toString());


        int level = (int) Math.round((double)getItem(i).get("user_occurrence_level"));
        changeLevelColor(level, (ImageView) result.findViewById(R.id.list_image));

        return result;
    }

    private void changeLevelColor(int level, ImageView v){
        switch (level){
            case 1:
                v.setBackgroundColor(Color.parseColor("#12CC4A"));
                break;
            case 2:
                v.setBackgroundColor(Color.rgb(178,255,102));
                break;
            case 3:
                v.setBackgroundColor(Color.parseColor("#FFDD00"));
                break;
            case 4:
                v.setBackgroundColor(Color.rgb(255, 153, 51));
                break;
            case 5:
                v.setBackgroundColor(Color.parseColor("#DE0000"));
                break;
        }

    }
}
