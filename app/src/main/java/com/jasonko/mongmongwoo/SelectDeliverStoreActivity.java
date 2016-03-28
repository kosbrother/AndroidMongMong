package com.jasonko.mongmongwoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jasonko.mongmongwoo.adpters.StoreGridAdapter;
import com.jasonko.mongmongwoo.api.MapApi;
import com.jasonko.mongmongwoo.api.StoreApi;
import com.jasonko.mongmongwoo.model.County;
import com.jasonko.mongmongwoo.model.Road;
import com.jasonko.mongmongwoo.model.Store;
import com.jasonko.mongmongwoo.model.Town;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/11/16.
 */
public class SelectDeliverStoreActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng storeLatLng;

    private Spinner countySpinners;
    private Spinner townSpinners;
    private Spinner roadSpinners;
    private GridView storeGridView;
    private TextView storeName;
    private TextView storeAddress;
    private Button selectStoreButton;
    private LinearLayout linearMap;

    private int county_id;
    private int town_id;
    private int road_id;
    private Store theSelectedStore;

    private ArrayList<Town> townArray;
    private ArrayList<Road> roadArray;
    private ArrayList<Store> storeArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_deliver_store);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("選擇超商地址");

        countySpinners = (Spinner) findViewById(R.id.spinner1);
        townSpinners = (Spinner) findViewById(R.id.spinner2);
        roadSpinners = (Spinner) findViewById(R.id.spinner3);
        storeGridView = (GridView) findViewById(R.id.store_grid);
        storeName = (TextView) findViewById(R.id.select_store_name);
        storeAddress = (TextView) findViewById(R.id.select_store_address);
        selectStoreButton = (Button) findViewById(R.id.select_store_button);
        linearMap = (LinearLayout) findViewById(R.id.linear_store_map);

        final ArrayList<County> counties = StoreApi.getCounties();
        ArrayAdapter<County> countyArrayAdapter = new ArrayAdapter<>(this, R.layout.myspinner, counties);
        countyArrayAdapter.setDropDownViewResource(R.layout.myspinner);
        countySpinners.setAdapter(countyArrayAdapter);

        countySpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                county_id = counties.get(position).getCounty_id();
                new TownsTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        townSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                town_id = townArray.get(position).getTown_id();
                new RoadsTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        roadSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                road_id = roadArray.get(position).getRoad_id();
                new StoresTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        selectStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundleBack = new Bundle();
                bundleBack.putSerializable("Selected_Store", theSelectedStore);
                intent.putExtras(bundleBack);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
    }

    public void setStoreInfo(Store theStore){
        theSelectedStore = theStore;
        storeName.setText(theSelectedStore.getName());
        storeAddress.setText(theSelectedStore.getAddress());
        linearMap.setVisibility(View.VISIBLE);
        new NewsTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private class NewsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            storeLatLng = MapApi.getLatLanFromAddress(theSelectedStore.getAddress());
            if (storeLatLng != null) {
                Log.i("DeliverStoreActivity", storeLatLng.toString());
            }
            return true;
        }

        @Override
        protected void onPostExecute(Object result) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(storeLatLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(storeLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        }
    }


    private class TownsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            townArray = StoreApi.getTowns(county_id);
            return true;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (townArray != null){
                ArrayAdapter<Town> townArrayAdapter = new ArrayAdapter<>(SelectDeliverStoreActivity.this, R.layout.myspinner, townArray);
                townArrayAdapter.setDropDownViewResource(R.layout.myspinner);
                townSpinners.setAdapter(townArrayAdapter);
            }
        }
    }

    private class RoadsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            roadArray = StoreApi.getRoads(county_id,town_id);
            return true;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (roadArray != null){
                ArrayAdapter<Road> roadArrayAdapter = new ArrayAdapter<>(SelectDeliverStoreActivity.this, R.layout.myspinner, roadArray);
                roadArrayAdapter.setDropDownViewResource(R.layout.myspinner);
                roadSpinners.setAdapter(roadArrayAdapter);
            }
        }
    }

    private class StoresTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            storeArray = StoreApi.getStores(county_id,town_id,road_id);
            return true;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (storeArray!=null){
                StoreGridAdapter adapter = new StoreGridAdapter(SelectDeliverStoreActivity.this, storeArray);
                storeGridView.setAdapter(adapter);
            }else {
                Toast.makeText(SelectDeliverStoreActivity.this,"此區無寄送店面", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
