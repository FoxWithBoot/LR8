package com.example.lr8_0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText[] editText = new EditText[2];
    private Button[] buttonS = new Button[2];
    private Button buttonWay;
    private ListView[] list = new ListView[2];
    private MapView[] mapView = new MapView[2];

    private List<Place> addresses;
    private int index;
    private GoogleMap[] map = new GoogleMap[2];
    private Double[] lat = new Double[2];
    private Double[] lng = new Double[2];

    private final static String KEY = "AIzaSyAI4nxhTP5r6zfpS5cgEJ63k4uNw3wzaDs";
    private final static String WWW = "https://maps.googleapis.com/maps/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = findViewById(R.id.tabhost);
        tabHost.setup(); //инициализация

        TabHost.TabSpec tabSpec;

        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec("tag1");
        // название вкладки
        tabSpec.setIndicator("Откуда");
        // указываем id компонента из FrameLayout, он и станет содержимым
        tabSpec.setContent(R.id.tab1);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);

        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec("tag2");
        // название вкладки
        tabSpec.setIndicator("Куда");
        // указываем id компонента из FrameLayout, он и станет содержимым
        tabSpec.setContent(R.id.tab2);
        // добавляем в корневой элемент
        tabHost.addTab(tabSpec);

        editText[0] = findViewById(R.id.editText);
        editText[1] = findViewById(R.id.editText2);
        buttonS[0] = findViewById(R.id.button1);
        buttonS[1] = findViewById(R.id.button2);
        buttonWay = findViewById(R.id.button3);
        list[0] = findViewById(R.id.list1);
        list[1] = findViewById(R.id.list2);
        mapView[0] = findViewById(R.id.map1);
        mapView[1] = findViewById(R.id.map);



        buttonWay.setEnabled(false);

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.button1) index=0;
                else index=1;
                Log.d("MyShit ", "BBBB");
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(WWW)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                Service serv = retrofit.create(Service.class);
                Call<Results> places = serv.getAddress(editText[index].getText().toString(), KEY);

                addresses = new ArrayList<>();

                places.enqueue(new Callback<Results>() {
                    @Override
                    public void onResponse(Call<Results> call, Response<Results> response) {
                        if (response.isSuccessful()) {
                            Log.d("MyShit ","response ");
                            Results res = response.body();
                            if(res!=null) {
                                if(res.adressList.get(0).geometry!=null) {
                                    for (int i = 0; i < res.adressList.size(); i++) {
                                        Log.d("MyShit ", "CCCCC");
                                        Place p = new Place();
                                        p.address = res.adressList.get(i).name;
                                        p.lat = res.adressList.get(i).geometry.location.lat;
                                        p.lng = res.adressList.get(i).geometry.location.lng;
                                        addresses.add(p);
                                    }
                                }else list[index].setAdapter(null);
                                if(addresses.size()>0){
                                    Log.d("MyShit ", "AAAAAA");
                                    ArrayList<String> p = new ArrayList<>();
                                    for(int i=0; i<addresses.size(); i++){
                                        p.add(addresses.get(i).address);
                                        Log.d("MyShitAAAA ",addresses.get(i).address);
                                    }
                                    ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, p);
                                    list[index].setAdapter(adapter);
                                }
                            }else list[index].setAdapter(null);
                        }else {
                            Log.d("MyShit ","response code " + response.code());
                            list[index].setAdapter(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<Results> call, Throwable t) {
                        Log.d("MyShit","failure " + t);
                        list[index].setAdapter(null);
                    }
                });


            }
        };
        buttonS[0].setOnClickListener(click);
        buttonS[1].setOnClickListener(click);

        AdapterView.OnItemClickListener clickItem = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lat[index] = addresses.get(position).lat;
                lng[index] = addresses.get(position).lng;

                mapView[index].onCreate(savedInstanceState);
                mapView[index].onResume();
                try{
                    MapsInitializer.initialize(getApplicationContext());
                }catch(Exception e){
                    e.printStackTrace();
                }
                mapView[index].getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map[index]=googleMap;
                        map[index].clear();
                        LatLng coord = new LatLng(addresses.get(position).lat, addresses.get(position).lng);
                        map[index].addMarker(new MarkerOptions().position(coord));
                        map[index].moveCamera(CameraUpdateFactory.newLatLng(coord));
                        UiSettings u = map[index].getUiSettings();
                        u.setZoomControlsEnabled(true);
                        u.setZoomGesturesEnabled(true);

                    }
                });
                if((lat[0]!=null)&&(lng[1]!=null)) buttonWay.setEnabled(true);
                else buttonWay.setEnabled(false);
            }
        };
        list[0].setOnItemClickListener(clickItem);
        list[1].setOnItemClickListener(clickItem);

        buttonWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WayActivity.class);
                intent.putExtra("latA", lat[0]);
                intent.putExtra("lngA", lng[0]);
                intent.putExtra("latB", lat[1]);
                intent.putExtra("lngB", lng[1]);
                startActivity(intent);
            }
        });
    }
}
