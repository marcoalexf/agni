package com.example.marisco.myapplication;

import  android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
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
public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String ENDPOINT = "https://custom-tine-204615.appspot.com/rest/";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String VISIBILITY = "visibility";
    private static final String LEVEL = "level";

    private GoogleMap map;
    @BindView(R.id.mapView) MapView mapView;
    @BindView(R.id.filters_button) ImageButton filter_button;

    private List<Map<String, Object>> map_list;
    private Retrofit retrofit;

    private Map<Marker, Map<String, Object>> marker_list;

    private Location initialLoc;
    private int minDifficulty;
    private boolean cleanIsChecked, zoneIsChecked, otherIsChecked;

    private LayoutInflater inflater;
    private ViewGroup container;

    public MapFragment() {
        minDifficulty = 1;
        cleanIsChecked = true;
        zoneIsChecked = true;
        otherIsChecked = true;
        marker_list = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;
        this.container = container;

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, v);

        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately
        mapView.getMapAsync(this);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow();
            }
        });
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
        //map.getUiSettings().setMyLocationButtonEnabled(false);
        getOccurrences();
    }

    private void putAllMarkers(){
        map.clear();
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                listOccurrenceDetails(marker_list.get(marker));
            }
        });
        for(Map<String, Object> entry: map_list){
            if(isNotFiltered(entry))
                putMarker(entry);
        }
    }

    private boolean isNotFiltered(Map<String, Object> entry){
        if((int) Math.round((double)entry.get("user_occurrence_level")) >= minDifficulty){
            if( (cleanIsChecked && entry.get("user_occurrence_type").equals(getResources().getString(R.string.occurrence_type_clean))) ||
                    (zoneIsChecked && entry.get("user_occurrence_type").equals(getResources().getString(R.string.occurrence_type_zone))) ||
                    (otherIsChecked && entry.get("user_occurrence_type").equals(getResources().getString(R.string.occurrence_type_other)))){
                return true;
            }
        }
        return false;
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

    private void popupWindow() {
        View popupView = inflater.inflate(R.layout.filter_menu, container, false);
        setRadioIds(popupView);
        final RadioGroup level_rb = popupView.findViewById(R.id.minimum_level_rb);

        final CheckBox cb_clean = popupView.findViewById(R.id.cb_clean);
        final CheckBox cb_zone = popupView.findViewById(R.id.cb_zone);
        final CheckBox cb_other = popupView.findViewById(R.id.cb_other);

        cb_clean.setChecked(cleanIsChecked);
        cb_zone.setChecked(zoneIsChecked);
        cb_other.setChecked(otherIsChecked);
        level_rb.check(minDifficulty);

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final PopupWindow popupWindow = new PopupWindow(popupView, popupView.getMeasuredWidth(), popupView.getMeasuredHeight(), true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setIgnoreCheekPress();

        Button btnOk = (Button) popupView.findViewById(R.id.btnApply);
        Button btnCancel = (Button) popupView.findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minDifficulty = level_rb.getCheckedRadioButtonId();
                cleanIsChecked = cb_clean.isChecked();
                zoneIsChecked = cb_zone.isChecked();
                otherIsChecked = cb_other.isChecked();
                putAllMarkers();
                popupWindow.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    @SuppressLint("ResourceType")
    private void setRadioIds(View popupView) {
        popupView.findViewById(R.id.radio1).setId(1);
        popupView.findViewById(R.id.radio2).setId(2);
        popupView.findViewById(R.id.radio3).setId(3);
        popupView.findViewById(R.id.radio4).setId(4);
        popupView.findViewById(R.id.radio5).setId(5);
    }
}
