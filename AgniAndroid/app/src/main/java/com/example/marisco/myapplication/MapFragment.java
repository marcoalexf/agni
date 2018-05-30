package com.example.marisco.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
public class MapFragment extends Fragment implements OnMapReadyCallback{

    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";

    private GoogleMap map;
    @BindView(R.id.mapView) MapView mapView;

    private ArrayList<Map<String, Object>> map_list;
    private Retrofit retrofit;

    private List<MarkerOptions> marker_list;

    public MapFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, v);

        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately
        mapView.getMapAsync(this);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);





        return v;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) throws SecurityException{
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);

                } else {
                    //meter mapa em local standard
                }
                return;
            }
        }
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
                    map_list = new ArrayList<>(response.body());
                    putAllMarkers();
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

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        getOccurrences();
    }

    private void putAllMarkers(){

        for(Map<String, Object> entry: map_list){
            putMarker((String) entry.get("user_occurrence_title"), (double)entry.get("user_occurrence_lat"),
                    (double)entry.get("user_occurrence_lon"));
        }
    }

    private void putMarker(String title, double lat, double lon) {
        MarkerOptions mp = new MarkerOptions();
        mp.position(new LatLng(lat, lon));
        mp.title(title);
        map.addMarker(mp);
    }
}
