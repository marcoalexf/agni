package com.example.marisco.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListOccurrences extends Fragment {

    private static final String TOKEN = "token";
    public static final String RESPONSE = "com.example.marisco.myapplication.RESPONSE";
    private static final String ENDPOINT = "https://liquid-layout-196103.appspot.com/rest/";
    private LoginResponse token;

    @BindView(R.id.list_occurrences)
    EditText list_occurrences;
    private Retrofit retrofit;

    public ListOccurrences(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
        Bundle b = this.getArguments();
        if(b != null){
            this.token = (LoginResponse) b.getSerializable(TOKEN);
        }
        getOccurrences();

        return v;
    }

    private void getOccurrences(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        AgniAPI agniAPI = retrofit.create(AgniAPI.class);

        ListOccurrenceData data = new ListOccurrenceData(token,true, token.username);
        Call<List<Map<String, Object>> > call = agniAPI.getOccurrences(data);

        call.enqueue(new Callback<List<Map<String, Object>> >() {
            @Override
            public void onResponse(Call<List<Map<String, Object>> > call, Response<List<Map<String, Object>> > response) {
                if (response.code() == 200){
                    processList(response.body());
                }

                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get occurrences" + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>> > call, Throwable t) {
                Log.e("ERROR", t.toString());
            }
        });
    }

    private void processList(List<Map<String, Object>>  occurrences){

    }
}
