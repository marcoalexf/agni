package com.example.marisco.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListOccurrences extends Fragment {

    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String VISIBILITY = "visibility";
    private static final String LEVEL = "level";

    private Retrofit retrofit;

    @BindView(R.id.list_occurrences) ListView lv;

    private ArrayList<Map<String, Object>> map_list;

    public ListOccurrences() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_ocurrences, container, false);
        ButterKnife.bind(this, v);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
                listOccurrenceDetails(adapter, v, position);
            }
        });

        getOccurrences();
        return v;
    }

    private void listOccurrenceDetails(AdapterView<?> adapter, View v, int position){

        OccurrenceDetails od = new OccurrenceDetails();
        FragmentManager fman = getFragmentManager();
        Bundle args = new Bundle();
        args.putSerializable(TITLE, (String) map_list.get(position).get("user_occurrence_title"));
        args.putSerializable(DESCRIPTION, (String) map_list.get(position).get("user_occurrence_description"));
        args.putSerializable(LEVEL, (double) map_list.get(position).get("user_occurrence_level"));
        args.putSerializable(VISIBILITY, (boolean) map_list.get(position).get("user_occurrence_visibility"));
        args.putSerializable(LATITUDE, (double) map_list.get(position).get("user_occurrence_lat"));
        args.putSerializable(LONGITUDE, (double) map_list.get(position).get("user_occurrence_lon"));

        od.setArguments(args);
        fman.beginTransaction().replace(R.id.fragment, od).commit();
    }

    private void getOccurrences(){
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
                    map_list = new ArrayList<>(response.body());
                    ListAdapterOccurrence adapter = new ListAdapterOccurrence(getContext(), map_list);
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
    }
}
