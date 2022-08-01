package com.example.phototomap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class FragmentMaps extends Fragment implements OnMapReadyCallback {

    private ArrayList<MyLocation> marker_list = new ArrayList<>();
    private GoogleMap googleMap;
    private MapView mapView;

    public FragmentMaps() {}

    public FragmentMaps(ArrayList<MyLocation> location_list) {
        marker_list = location_list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    public void refreshMarkers(ArrayList<MyLocation> location_list) {
        if (!marker_list.isEmpty()) { marker_list.clear(); }
        marker_list.addAll(location_list);
        addAMarker();
    }

    private void addAMarker(){
        if(!marker_list.isEmpty()) {
            for (MyLocation myLocation : marker_list) {
                LatLng latLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(myLocation.getId())));
            }

            MyLocation lastLocation = marker_list.get(marker_list.size() - 1);
            LatLng defaultPos = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultPos));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            ((MainActivity)getActivity()).displayMarkerDetail(lastLocation.getLatitude(),lastLocation.getLongitude(),lastLocation.getBitmap());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (!marker_list.isEmpty()) { addAMarker(); }
        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Integer location_id = Integer.parseInt(marker.getTitle());
                for (MyLocation myLocation : marker_list) {
                    if (location_id.equals(myLocation.getId())) {
                        ((MainActivity)getActivity()).displayMarkerDetail(myLocation.getLatitude(), myLocation.getLongitude(), myLocation.getBitmap());
                    }
                }
                return true;
            }
        });
    }
}