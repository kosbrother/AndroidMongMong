package com.jasonko.mongmongwoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jasonko.mongmongwoo.adpters.StoreGridAdapter;
import com.jasonko.mongmongwoo.api.DensityApi;
import com.jasonko.mongmongwoo.api.StoreApi;
import com.jasonko.mongmongwoo.model.County;
import com.jasonko.mongmongwoo.model.Road;
import com.jasonko.mongmongwoo.model.Store;
import com.jasonko.mongmongwoo.model.Town;
import com.jasonko.mongmongwoo.utils.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/11/16.
 */
public class SelectDeliverStoreActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
//    private LatLng storeLatLng;

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

    private StoreGridAdapter storeGridAdapter;
    private ProgressBar myProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_deliver_store);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Activity Select Store");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("選擇超商地址");

        countySpinners = (Spinner) findViewById(R.id.spinner1);
        townSpinners = (Spinner) findViewById(R.id.spinner2);
        roadSpinners = (Spinner) findViewById(R.id.spinner3);
        storeGridView = (GridView) findViewById(R.id.store_grid);
        storeName = (TextView) findViewById(R.id.select_store_name);
        storeAddress = (TextView) findViewById(R.id.select_store_address);
        selectStoreButton = (Button) findViewById(R.id.select_store_button);
        linearMap = (LinearLayout) findViewById(R.id.linear_store_map);
        myProgressBar = (ProgressBar) findViewById(R.id.my_progress_bar);

        final ArrayList<County> counties = StoreApi.getCounties();
        ArrayAdapter<County> countyArrayAdapter = new ArrayAdapter<>(this, R.layout.myspinner, counties);
        countyArrayAdapter.setDropDownViewResource(R.layout.myspinner);
        countySpinners.setAdapter(countyArrayAdapter);

        countySpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (NetworkUtil.getConnectivityStatus(SelectDeliverStoreActivity.this)!=0) {
                    county_id = counties.get(position).getCounty_id();
                    linearMap.setVisibility(View.GONE);
                    selectStoreButton.setVisibility(View.GONE);
                    if (storeGridAdapter != null) {
                        storeGridAdapter.resetSelectedStorePosition();
                        storeArray.clear();
                        storeGridAdapter.notifyDataSetChanged();
                    }
                    new TownsTask().execute();
                }else {
                    Toast.makeText(SelectDeliverStoreActivity.this,"無網路連線", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        townSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (NetworkUtil.getConnectivityStatus(SelectDeliverStoreActivity.this)!=0) {
                    town_id = townArray.get(position).getTown_id();
                    linearMap.setVisibility(View.GONE);
                    selectStoreButton.setVisibility(View.GONE);
                    if (storeGridAdapter != null) {
                        storeGridAdapter.resetSelectedStorePosition();
                        storeArray.clear();
                        storeGridAdapter.notifyDataSetChanged();
                    }
                    new RoadsTask().execute();
                }else {
                    Toast.makeText(SelectDeliverStoreActivity.this,"無網路連線", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        roadSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (NetworkUtil.getConnectivityStatus(SelectDeliverStoreActivity.this)!=0) {
                    road_id = roadArray.get(position).getRoad_id();
                    linearMap.setVisibility(View.GONE);
                    selectStoreButton.setVisibility(View.GONE);
                    if (storeGridAdapter != null) {
                        storeGridAdapter.resetSelectedStorePosition();
                        storeArray.clear();
                        storeGridAdapter.notifyDataSetChanged();
                    }
                    new StoresTask().execute();
                }else {
                    Toast.makeText(SelectDeliverStoreActivity.this,"無網路連線", Toast.LENGTH_SHORT).show();
                }
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
        selectStoreButton.setVisibility(View.VISIBLE);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(theStore.getLatLng()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(theStore.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        return super.onOptionsItemSelected(menuItem);
    }



    private class TownsTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myProgressBar.setVisibility(View.VISIBLE);
        }

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
        protected void onPreExecute() {
            super.onPreExecute();
            myProgressBar.setVisibility(View.VISIBLE);
        }

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
        protected void onPreExecute() {
            super.onPreExecute();
            myProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            storeArray = StoreApi.getStores(county_id,town_id,road_id);
            return true;
        }

        @Override
        protected void onPostExecute(Object result) {
            myProgressBar.setVisibility(View.GONE);
            if (storeArray!=null){
                storeGridAdapter = new StoreGridAdapter(SelectDeliverStoreActivity.this, storeArray);
                storeGridView.setAdapter(storeGridAdapter);

                ViewGroup.LayoutParams params= storeGridView.getLayoutParams();
                int height_size;
                if (storeArray.size()%2 == 0){
                    height_size = storeArray.size()/2;
                }else {
                    height_size = storeArray.size()/2 + 1;
                }
                params.height= (int) DensityApi.convertDpToPixel(55 * height_size, SelectDeliverStoreActivity.this);
                storeGridView.setLayoutParams(params);
            }else {
                Toast.makeText(SelectDeliverStoreActivity.this,"此區無寄送店面", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
