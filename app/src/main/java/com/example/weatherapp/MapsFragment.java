package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class MapsFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            TileProvider tileProvider = new UrlTileProvider(256, 256) {

                @Override
                public URL getTileUrl(int x, int y, int zoom) {

                    /* Define the URL pattern for the tile images */
                    String s = String.format("https://tile.openweathermap.org/map/temp_new/%d/%d/%d.png?appid=302b92d8418323d0da4b24d83e4a82e0", zoom, x, y);

                    if (!checkTileExists(x, y, zoom)) {
                        return null;
                    }

                    try {
                        return new URL(s);
                    } catch (MalformedURLException e) {
                        throw new AssertionError(e);
                    }
                }

                /*
                 * Check that the tile server supports the requested x, y and zoom.
                 * Complete this stub according to the tile range you support.
                 * If you support a limited range of tiles at different zoom levels, then you
                 * need to define the supported x, y range at each zoom level.
                 */
                private boolean checkTileExists(int x, int y, int zoom) {


                    return true;
                }
            };
            WeatherData w = SpeechToText.getWeatherInstance();

            if(w == null){

                w = TextSpeech.getWeatherInstance();

            }
            Double lat = Double.parseDouble(w.getLat());
            Double lon = Double.parseDouble(w.getLon());
            LatLng loc = new LatLng(lat,lon);
            googleMap.addMarker(new MarkerOptions().position(loc).title("You are here!"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(5));
            TileOverlay tileOverlay = googleMap.addTileOverlay(new TileOverlayOptions()
                    .tileProvider(tileProvider));


        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }
}