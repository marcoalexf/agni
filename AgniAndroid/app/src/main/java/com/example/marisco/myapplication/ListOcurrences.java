package com.example.marisco.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListOcurrences extends Fragment {

    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";

    Retrofit retrofit;

    @BindView(R.id.list_occurrences) ListView lv;

    List<Map<String, Object>> occurrences = new ArrayList<Map<String, Object>>();

    public ListOcurrences() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_ocurrences, container, false);
        ButterKnife.bind(this, v);

        //Get request to get ocurrences

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        Call<List<Map<String, Object>>> call = agniAPI.getOccurrences();

        call.enqueue(new Callback<List<Map<String, Object>>>() {
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.code() == 200) {
                    Log.d("GET_PUBLIC_OCCURRENCES", response.toString());
                    ArrayList<Map<String, Object>> map_list = new ArrayList<>(response.body());
                    for (Map m: map_list) {
                        Log.d("OCCURENCE: ", m.toString());
                    }
                    ListAdapaterOccurrence adapter = new ListAdapaterOccurrence(getContext(), map_list);
                    lv.setAdapter(adapter);
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get public occurrences" + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });

        return v;
    }

}
