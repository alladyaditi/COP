package com.example.weatherapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Belal on 1/23/2018.
 */
 
public class SpeechToText extends Fragment implements OnSuccessListener<Location> {



    //Bundle key
    public static final String DATA_TAG = "weather.data.go";

    public static WeatherData data = new WeatherData();
    //Used for getting location
    private FusedLocationProviderClient fusedLocationClient;
    private final int REQ_CODE = 100;
    //Declare UI elements
    private Button goButton;
    private ImageButton locButton;
    private TextView idTextView, cities, temp, min, feels, max;
    private EditText locEditText;
    FloatingActionButton speak;

    //Volley request queue;
    private RequestQueue queue;

    //LOCATION REQUEST CODE KEY, not important since only asking for a single permission and don't need to distinguish
    public static final int LOCATION_REQUEST_CODE = 11;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard

        View view = inflater.inflate(R.layout.activity_main, null);
        goButton    = view.findViewById(R.id.goButton);
        locButton   = view.findViewById(R.id.locButton);
        idTextView  = view.findViewById(R.id.textView2);
        locEditText = view.findViewById(R.id.locEditText);
        speak       = view.findViewById(R.id.speak);


        queue = Volley.newRequestQueue(getContext());


        cities = view.findViewById(R.id.cityView);
        temp = view.findViewById(R.id.tempView);
        min =  view.findViewById(R.id.minView);
        feels = view.findViewById(R.id.feelsView);
        max = view.findViewById(R.id.maxView);



        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             onGoClick();
            }
        });





        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Attach references to UI elements


        //Instantiate the request queue





    }




    public static WeatherData getWeatherInstance(){
        return data;
    }

    public void onLocClick(View v){
        System.out.println("Location Button Clicked");

        //Ask for location permission
        if (ContextCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            getLocation();
            System.out.println("PERMISSION ALREADY GRANTED FOR LOCATION, DOING ACTION");
        }else {
            //directly ask for the permission.
            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION },LOCATION_REQUEST_CODE);


        }
    }

    public void onGoClick(){
        System.out.println("Go Button Clicked");
        String text = locEditText.getText().toString();
        int zipcode;
        try{
            System.out.println("ZIPCODE DETECTED!");
            zipcode = Integer.parseInt(text);
            getWeatherByZip(zipcode);
        }
        catch(Exception e){
            //Opps, try by city
            System.out.println("CITY DETECTED!");
            getWeatherByCity(text);
        }

    }



    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        System.out.println("onRequestPermissionsResult Callback Entered");

        //Check that permission was granted
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getLocation();
        }


    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(),this);


    }


    @Override
    //onSuccess for Location Services
    public void onSuccess(Location location) {
        //Get Weather by Location
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        System.out.println("Lattitude = " + lat);
        System.out.println("Longitude = " + lon);
        data.setLat(lat);
        data.setLon(lon);
        getWeatherByLocation(lat,lon);

    }

    public void getWeatherByCity(String city){
       String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+",us&appid=1867329547bcd808751dd2acaa7a1dd6";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject main = null;
                        try {
                            main = response.getJSONObject("main");
                            JSONObject coords = response.getJSONObject("coord");
                            data.setLat(coords.getString("lat"));
                            data.setLon(coords.getString("lon"));
                            data.setTemp(main.getString("temp"));
                            data.setFeelsLike(main.getString("feels_like"));
                            data.setCityName(response.getString("name"));
                            data.setTempMax(main.getString("temp_max"));
                            data.setTempMin(main.getString("temp_min"));
                            //TODO : Bundle the weather object and send to next activity
                            //Current implementation is just using static member




                            cities.setText("Weather for " + data.getCityName());
                            temp.setText("Temperature : " + String.valueOf(Math.floor(KtoF(Double.parseDouble(data.getTemp())))) + "F");
                            min.setText("Low : " + String.valueOf(Math.floor(KtoF(Double.parseDouble(data.getTempMin()))))+ "F");
                            max.setText("Maximum : " + String.valueOf(Math.floor(KtoF(Double.parseDouble(data.getTempMax())))) + "F");
                            feels.setText("Feels like : " + String.valueOf(Math.floor(KtoF(Double.parseDouble(data.getFeelsLike()))))+ "F");






                        } catch (JSONException e) {
                            System.out.println("JSON EXPLOSION");
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERROR WITH VOLLEY REQUEST");

                    }
                });

        queue.add(jsonObjectRequest);

    }

    public void getWeatherByZip(int zip){
        String zipcode = String.valueOf(zip);
        String url = getString(R.string.WEATHER_API_URL_ZIP) + zipcode + getString(R.string.WEATHER_API_KEY);
        System.out.println(url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject main = null;
                        try {
                            main = response.getJSONObject("main");
                            JSONObject coords = response.getJSONObject("coord");
                            data.setLat(coords.getString("lat"));
                            data.setLon(coords.getString("lon"));
                            data.setTemp(main.getString("temp"));
                            data.setFeelsLike(main.getString("feels_like"));
                            data.setCityName(response.getString("name"));
                            data.setTempMax(main.getString("temp_max"));
                            data.setTempMin(main.getString("temp_min"));
                            //TODO : Bundle the weather object and send to next activity
                            //Current implementation is just using static member





                        } catch (JSONException e) {
                            System.out.println("JSON EXPLOSION");
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERROR WITH VOLLEY REQUEST");

                    }
                });

        queue.add(jsonObjectRequest);




    }

    public void getWeatherByLocation(String lat,String lon){
        String url = getString(R.string.WEATHER_API_URL_LAT) + lat + getString(R.string.WEATHER_LON_SUFFIX) + lon
                + getString(R.string.WEATHER_API_KEY);

        System.out.println(url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject main = null;
                        try {
                            main = response.getJSONObject("main");
                            data.setTemp(main.getString("temp"));
                            data.setFeelsLike(main.getString("feels_like"));
                            data.setCityName(response.getString("name"));
                            data.setTempMax(main.getString("temp_max"));
                            data.setTempMin(main.getString("temp_min"));
                            //TODO : Bundle the weather object and send to next activity
                            //Current implementation is just using static member

                            Intent intent = new Intent(getContext(), DisplayActivity.class);
                            startActivity(intent);




                        } catch (JSONException e) {
                            System.out.println("JSON EXPLOSION");
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("ERROR WITH VOLLEY REQUEST");

                    }
                });

        queue.add(jsonObjectRequest);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    locEditText.setText((CharSequence) result.get(0));
                }
                break;
            }
        }
    }



    public static double KtoF(Double k){
        return ((k - 273.15) * 9/5 + 32);
    }








}