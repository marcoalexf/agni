package com.example.marisco.myapplication.constructors;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class CustomRenderer<T extends ClusterItem> extends DefaultClusterRenderer<T> {

    public CustomRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(T item,
                                               MarkerOptions markerOptions) {

        String level = item.getSnippet();
          switch (level){
        case "1":
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(130.0f));
            break;
        case "2":
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(80.0f));
            break;
        case "3":
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(50.0f));
            break;
        case "4":
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            break;
        case "5":
            markerOptions.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
            break;

        }
    }
}