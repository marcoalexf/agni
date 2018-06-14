package com.example.marisco.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
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
public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String VISIBILITY = "visibility";
    private static final String LEVEL = "level";

    private GoogleMap map;
    @BindView(R.id.mapView) MapView mapView;

    private List<Map<String, Object>> map_list;
    private Retrofit retrofit;

    private Map<Marker, Map<String, Object>> marker_list;

    private Location initialLoc;

    public MapFragment() {
        marker_list = new HashMap<>();
    }

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

                    map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        public void onMyLocationChange(Location location) {
                            if (initialLoc == null) {
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                                        location.getLongitude()), 14));
                                initialLoc = location;
                            }
                        }
                    });
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

        Call<CursorList> call = agniAPI.getOccurrences();

        call.enqueue(new Callback<CursorList>() {
            public void onResponse(Call<CursorList> call, Response<CursorList> response) {
                if (response.code() == 200) {
                    CursorList c = response.body();
                    map_list = c.getMapList();
                    putAllMarkers();
                }
                else {
                    Toast toast = Toast.makeText(getActivity(), "Failed to get public occurrences" + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            public void onFailure(Call<CursorList> call, Throwable t) {
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
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                listOccurrenceDetails(marker_list.get(marker));
            }
        });
        for(Map<String, Object> entry: map_list){
            putMarker(entry);
        }
    }

    private void putMarker(Map<String, Object> entry) {
        MarkerOptions mp = new MarkerOptions();
        mp.position(new LatLng((double)entry.get("user_occurrence_lat"), (double)entry.get("user_occurrence_lon")));
        mp.title((String) entry.get("user_occurrence_title"));
        Marker m = map.addMarker(mp);


        switch ((int) Math.round((double)entry.get("user_occurrence_level"))){
            case 1:
                m.setIcon(BitmapDescriptorFactory
                        .defaultMarker(130.0f));
                break;
            case 2:
                m.setIcon(BitmapDescriptorFactory
                        .defaultMarker(80.0f));
                break;
            case 3:
                m.setIcon(BitmapDescriptorFactory
                        .defaultMarker(50.0f));
                break;
            case 4:
                m.setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                break;
            case 5:
                m.setIcon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                break;
        }
        marker_list.put(m, entry);
        if(entry == null ){
            Toast toast = Toast.makeText(getActivity(), "entry é nula" , Toast.LENGTH_SHORT);
            toast.show();
        }
    }



    private void listOccurrenceDetails( Map<String, Object> entry){

        OccurrenceDetails od = new OccurrenceDetails();
        FragmentManager fman = getFragmentManager();
        Bundle args = new Bundle();

        if(entry!= null){
            args.putSerializable(TITLE, (String) entry.get("user_occurrence_title"));
            args.putSerializable(DESCRIPTION, (String) entry.get("user_occurrence_description"));
            args.putSerializable(LEVEL, (double) entry.get("user_occurrence_level"));
            args.putSerializable(VISIBILITY, (boolean) entry.get("user_occurrence_visibility"));
            args.putSerializable(LATITUDE, (double) entry.get("user_occurrence_lat"));
            args.putSerializable(LONGITUDE, (double) entry.get("user_occurrence_lon"));

            od.setArguments(args);
            fman.beginTransaction().replace(R.id.fragment, od).commit();
        }

    }

    @Override
    public void onLocationChanged(Location location){
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
