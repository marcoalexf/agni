package com.example.marisco.myapplication;

import  android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
    private static final String SAVED_LOCATIONS = "saved_locations";
    private static final String TOKEN = "token";
    private static final String MEDIA_IDS = "mediaIDs";
    private static final String ID = "occurrence_id";
    private static final String USER_ID = "userID";

    private GoogleMap map;
    @BindView(R.id.mapView) MapView mapView;
    @BindView(R.id.filters_button) ImageButton filter_button;
    @BindView(R.id.location_button) ImageButton location_button;

    private List<Map<String, Object>> map_list;
    private Retrofit retrofit;

    private List<SavedLocation> savedLocations;

    private Map<Marker, Map<String, Object>> marker_list;

    private Location initialLoc;
    private int minDifficulty;
    private boolean cleanIsChecked, zoneIsChecked, otherIsChecked;
    private String cursor;
    private LoginResponse token;
    private Marker locationMarker;

    private LayoutInflater inflater;
    private ViewGroup container;

    public MapFragment() {
        minDifficulty = 1;
        cleanIsChecked = true;
        zoneIsChecked = true;
        otherIsChecked = true;
        marker_list = new HashMap<>();
        savedLocations = new ArrayList<>(10);
        map_list = new LinkedList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        savedLocations = new ArrayList<>(10);

        Bundle b = this.getArguments();
        if (b != null) {
            this.token = (LoginResponse) b.getSerializable(TOKEN);
        }
        this.inflater = inflater;
        this.container = container;

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, v);

        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately
        mapView.getMapAsync(this);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        getSavedLocations();

        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtersWindow();
            }
        });

        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationMenu();
            }
        });
        return v;
    }

    private void getSavedLocations(){
        // load tasks from preference
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SAVED_LOCATIONS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(SAVED_LOCATIONS, null);
        Type type = new TypeToken<ArrayList<SavedLocation>>() {}.getType();
        savedLocations =  gson.fromJson(json, type);
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
                            }
                            initialLoc = location;
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

        VisibleRegion visibleRegion = map.getProjection().getVisibleRegion();
        LatLng nearLeft = visibleRegion.nearLeft;
        LatLng farRight = visibleRegion.farRight;

        float[] distanceHeight = new float[2];

        Location.distanceBetween(nearLeft.latitude, nearLeft.longitude, farRight.latitude,
                farRight.longitude, distanceHeight );

        float lat = (float)(nearLeft.latitude + farRight.latitude)/2;
        float lon = (float)(nearLeft.longitude + farRight.longitude)/2;

        /*Toast toast = Toast.makeText(getActivity(), "raio: " + (int)distanceHeight[0], Toast.LENGTH_SHORT);
        toast.show();

        Toast toast2 = Toast.makeText(getActivity(), "lat: " + lat, Toast.LENGTH_SHORT);
        toast2.show();

        Toast toast3 = Toast.makeText(getActivity(), "lon: " + lon, Toast.LENGTH_SHORT);
        toast3.show();*/
        if(Math.abs(lat) > 1 && Math.abs(lon) > 1){
            AgniAPI agniAPI = retrofit.create(AgniAPI.class);

            Call<CursorList> call = agniAPI.getMoreOccurrences(new ListOccurrenceData(token, false, null, cursor,
                    lat, lon, (int)distanceHeight[0]));

            call.enqueue(new Callback<CursorList>() {
                public void onResponse(Call<CursorList> call, Response<CursorList> response) {
                    if (response.code() == 200) {
                        CursorList c = response.body();
                        if(c.getMapList() != null)
                            map_list.addAll(c.getMapList());
                        cursor = c.getCursor();
                        if(!c.getMapList().isEmpty()) {
                            //Toast toast = Toast.makeText(getActivity(), "Ocorrências recebidas: " + map_list.size(), Toast.LENGTH_SHORT);
                            //toast.show();
                            putAllMarkers();
                        }
                    }
                    else {
                        Toast toast = Toast.makeText(getActivity(), "Failed to get public occurrences: " + response.code(), Toast.LENGTH_SHORT);
                        //toast.show();
                    }
                }
                public void onFailure(Call<CursorList> call, Throwable t) {
                    Log.e("ERROR", t.toString());
                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick (LatLng latLng){
                        mapPress(latLng);
                    }});
        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                cursor = null;
                getOccurrences();
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTitle().equals(getResources().getString(R.string.new_saved_location))){
                    saveLocationWindow();
                }
                else
                    listOccurrenceDetails(marker_list.get(marker));
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getOccurrences();
            }
        }, 2000);
    }

    private void putAllMarkers(){
        //map.clear();

        for(Map<String, Object> entry: map_list){
            if(isNotFiltered(entry))
                putMarker(entry);
        }
        getOccurrences();
    }

    private boolean isNotFiltered(Map<String, Object> entry){
        if((int) Math.round((double)entry.get("user_occurrence_level")) >= minDifficulty){
            if(cleanIsChecked && zoneIsChecked && otherIsChecked)
                return true;
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
            args.putSerializable(MEDIA_IDS, (ArrayList)entry.get("mediaIDs"));
            args.putSerializable(ID, (String)entry.get("occurrenceID"));
            args.putSerializable(USER_ID, (String) entry.get("userID"));

            od.setArguments(args);
            fman.beginTransaction().replace(R.id.fragment, od).commit();
        }
    }

    private void filtersWindow() {
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
                map.clear();
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

    private void locationMenu(){
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(getContext(), location_button);
        //Inflating the Popup using xml file
        popup.getMenu().add(getResources().getString(R.string.currentLocation));
        if(savedLocations != null){

            for(int i = 1; i < savedLocations.size() +1; i++){
                popup.getMenu().add(i, i, i, savedLocations.get(i-1).getTitle());
            }

        }
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getOrder() == 0){
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(new LatLng(initialLoc.getLatitude(),
                            initialLoc.getLongitude()), 15);
                    map.animateCamera(location);
                }else{
                    CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                            savedLocations.get(item.getOrder() -1).getLocation(), 15);
                    map.animateCamera(location);
                }
                getOccurrences();
                return true;
            }
        });
        popup.show();
    }


    private void saveNewLocation(String title, LatLng location){
        if(savedLocations == null)
            savedLocations = new ArrayList<>();

        savedLocations.add(new SavedLocation(title, location));

        SharedPreferences prefs = this.getActivity().getSharedPreferences(SAVED_LOCATIONS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(savedLocations);
        editor.putString(SAVED_LOCATIONS, json);
        editor.apply();
    }

    private void mapPress(final LatLng location){
        if(locationMarker != null)
            locationMarker.remove();

        MarkerOptions mp = new MarkerOptions();
        mp.position(location);
        mp.title(getResources().getString(R.string.new_saved_location));
        locationMarker = map.addMarker(mp);
        locationMarker.setIcon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
    }

    private void saveLocationWindow(){
        final View popupView = inflater.inflate(R.layout.save_location_menu, container, false);
        Button btnOk = (Button) popupView.findViewById(R.id.btnSave);
        Button btnCancel = (Button) popupView.findViewById(R.id.btnCancelSave);

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final PopupWindow popupWindow = new PopupWindow(popupView, popupView.getMeasuredWidth(), popupView.getMeasuredHeight(), true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setIgnoreCheekPress();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText locationName = (EditText) popupView.findViewById(R.id.locationName);
                String name = locationName.getText().toString();
                if(name != null && name.length() > 0){
                    saveNewLocation(name, locationMarker.getPosition());
                    popupWindow.dismiss();
                    locationMarker.remove();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationMarker.remove();
                popupWindow.dismiss();
            }
        });

    }
}
