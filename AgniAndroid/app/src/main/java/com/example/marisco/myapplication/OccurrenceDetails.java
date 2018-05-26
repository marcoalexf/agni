package com.example.marisco.myapplication;

import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OccurrenceDetails extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String VISIBILITY = "visibility";
    private static final String LEVEL = "level";

    private GoogleMap mapG;
    private MarkerOptions mp;
    private double lat, lon;
    String title;

    @BindView(R.id.detail_title)
    TextView o_title;
    @BindView(R.id.detail_description)
    TextView o_description;
    @BindView(R.id.detail_level)
    TextView o_level;
    @BindView(R.id.detail_visibility)
    TextView o_visibility;
    @BindView(R.id.occurrence_map)
    MapView map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.detail_occurrence, container, false);
        ButterKnife.bind(this, v);
        o_description.setText("teste descricao");
        Bundle b = this.getArguments();
        title = "";
        String description = "";
        lat = 0;
        lon = 0;
        boolean visibility = false;
        double level = 1;
        if (b != null) {
            title = (String)b.getSerializable(TITLE);
            description = (String)b.getSerializable(DESCRIPTION);
            lat = (double)b.getSerializable(LATITUDE);
            lon = (double)b.getSerializable(LONGITUDE);
            visibility = (boolean)b.getSerializable(VISIBILITY);
            level = (double)b.getSerializable(LEVEL);
        }

        map.onCreate(savedInstanceState);
        map.onResume(); // needed to get the map to display immediately
        map.getMapAsync(this);
        fillInfo(title, description, lat, lon, visibility, level);

        return v;
    }

    private void fillInfo(String title, String description, double lat, double lon, boolean visibility, double level){
        o_title.setText(title);
        o_description.setText(description);
        o_level.setText(level + "");
        if(visibility)
            o_visibility.setText(R.string.occurrence_public);
        else o_visibility.setText(R.string.occurrence_private);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.mapG = map;
        putMarker();
    }

    private void putMarker() {
        mapG.clear();

        mp = new MarkerOptions();

        mp.position(new LatLng(lat, lon));

        mp.title(title);

        mapG.addMarker(mp);

        mapG.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(lat, lon), 16));
    }


    @Override
    public void onLocationChanged(Location location) {

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
