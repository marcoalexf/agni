package com.example.marisco.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marisco.myapplication.constructors.ProfileUsernameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListAdapterComments extends ArrayAdapter {

    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";

    private List<Map<String, Object>> mData;
    Context context;
    private Retrofit retrofit;

    public ListAdapterComments(Context context, List<Map<String, Object>> list, int resource) {
        super(context, resource, list);
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
            result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment, viewGroup, false);
        } else {
            result = view;
        }

        Map<String, Object> temp = getItem(i);

        ((TextView) result.findViewById(R.id.comment_author)).setText(temp.get("comment_username").toString());
        ((TextView) result.findViewById(R.id.comment_text)).setText(temp.get("comment_text").toString());
        ((TextView) result.findViewById(R.id.comment_date)).setText(temp.get("comment_date").toString());

        return result;
    }
}
