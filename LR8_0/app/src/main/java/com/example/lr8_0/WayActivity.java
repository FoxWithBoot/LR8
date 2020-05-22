package com.example.lr8_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WayActivity extends AppCompatActivity {
    private static final String WWW = "https://maps.googleapis.com/maps/api/";
    private final static String KEY = "AIzaSyAI4nxhTP5r6zfpS5cgEJ63k4uNw3wzaDs";

    List<LatLng> way;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_way);
        Double latA = getIntent().getDoubleExtra("latA", 0);
        Double lngA = getIntent().getDoubleExtra("lngA", 0);
        Double latB = getIntent().getDoubleExtra("latB", 0);
        Double lngB = getIntent().getDoubleExtra("lngB", 0);

        MapView mapView = findViewById(R.id.map3);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try{
            MapsInitializer.initialize(getApplicationContext());
        }catch(Exception e){
            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(WWW)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                Service serv = retrofit.create(Service.class);
                Call<Road> road = serv.getWay(latA+","+lngA, latB+","+lngB, KEY);

                road.enqueue(new Callback<Road>() {
                    @Override
                    public void onResponse(Call<Road> call, Response<Road> response) {
                        if (response.isSuccessful()) {
                            Log.d("MyShit ", "response ");
                            if (response.body().status.equals("OK")) {
                                List<LatLng> mPoints = PolyUtil.decode(response.body().getPoints());

                                PolylineOptions line = new PolylineOptions();
                                line.width(4);

                                LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
                                for (int i = 0; i < mPoints.size(); i++) {

                                    line.add(mPoints.get(i));
                                    latLngBuilder.include(mPoints.get(i));
                                }
                                googleMap.addPolyline(line);
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(latA, lngA)));
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(latB, lngB)));

                                int size = getResources().getDisplayMetrics().widthPixels;
                                LatLngBounds latLngBounds = latLngBuilder.build();
                                CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 25);
                                googleMap.moveCamera(track);

                                UiSettings u = googleMap.getUiSettings();
                                u.setZoomControlsEnabled(true);
                                u.setZoomGesturesEnabled(true);
                            }else{
                                TextView textView = findViewById(R.id.text);
                                textView.setText("Not found");
                            }
                        }else Log.d("MyShit ","response code " + response.code());
                    }

                    @Override
                    public void onFailure(Call<Road> call, Throwable t) {
                        Log.d("MyShit","failure " + t);
                    }
                });
            }
        });
    }
}
