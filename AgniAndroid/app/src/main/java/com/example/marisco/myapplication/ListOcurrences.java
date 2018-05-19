package com.example.marisco.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

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

    public static final String ENDPOINT = "https://liquid-layout-196103.appspot.com/rest/";

    Retrofit retrofit;

    @BindView(R.id.list_occurrences) ListView lv;

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

        Call<ResponseBody> call = agniAPI.getAllPublicOccurrences();

        call.enqueue(new Callback<ResponseBody>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200)
                    Log.d("GET_PUBLIC_OCCURRENCES", response.toString());
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get public occurrences" + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });

        return v;
    }

}
