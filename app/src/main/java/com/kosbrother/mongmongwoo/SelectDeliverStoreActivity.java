package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kosbrother.mongmongwoo.adpters.StoreGridAdapter;
import com.kosbrother.mongmongwoo.api.DensityApi;
import com.kosbrother.mongmongwoo.api.StoreApi;
import com.kosbrother.mongmongwoo.model.County;
import com.kosbrother.mongmongwoo.model.Road;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.model.Town;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;

import java.util.List;

public class SelectDeliverStoreActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Spinner townSpinners;
    private Spinner roadSpinners;
    private Button selectStoreButton;

    private int countyId;
    private int townId;
    private int road_id;
    private Store selectedStore;

    private List<County> counties;
    private List<Town> townArray;
    private List<Road> roadArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counties = StoreApi.getCounties();
        setContentView(R.layout.activity_select_deliver_store);
        setToolBar();
        setMapFragment();
        setCountySpinner();
        setTownSpinner();
        setRoadSpinner();
        setSelectStoreButton();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void updateStoreInfo(Store theStore) {
        selectedStore = theStore;

        TextView storeName = (TextView) findViewById(R.id.select_store_name);
        storeName.setText(selectedStore.getName());
        TextView storeAddress = (TextView) findViewById(R.id.select_store_address);
        storeAddress.setText(selectedStore.getAddress());

        getLinearMap().setVisibility(View.VISIBLE);
        selectStoreButton.setVisibility(View.VISIBLE);

        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(theStore.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mMap.addMarker(new MarkerOptions()
                        .title(selectedStore.getName())
                        .position(cameraPosition.target)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_7_11)));
                mMap.setOnCameraChangeListener(null);
            }
        });
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setTitle("選擇超商地址");
    }

    private void setMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setCountySpinner() {
        Spinner countySpinners = (Spinner) findViewById(R.id.country_spinner);
        ArrayAdapter<County> countyArrayAdapter = new ArrayAdapter<>(this, R.layout.myspinner, counties);
        countyArrayAdapter.setDropDownViewResource(R.layout.myspinner);
        countySpinners.setAdapter(countyArrayAdapter);

        countySpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countyId = counties.get(position).getId();
                setLoadingLayout();
                new TownsTask().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setTownSpinner() {
        townSpinners = (Spinner) findViewById(R.id.town_spinner);
        townSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (townArray != null) {
                    townId = townArray.get(position).getId();
                    setLoadingLayout();
                    new RoadsTask().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setRoadSpinner() {
        roadSpinners = (Spinner) findViewById(R.id.road_spinner);
        roadSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (roadArray != null) {
                    road_id = roadArray.get(position).getId();
                    setLoadingLayout();
                    new StoresTask().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSelectStoreButton() {
        selectStoreButton = (Button) findViewById(R.id.select_store_button);
        selectStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundleBack = new Bundle();
                bundleBack.putSerializable("Selected_Store", selectedStore);
                intent.putExtras(bundleBack);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private LinearLayout getLinearMap() {
        return (LinearLayout) findViewById(R.id.linear_store_map);
    }

    private ProgressBar getProgressBar() {
        return (ProgressBar) findViewById(R.id.my_progress_bar);
    }

    private GridView getStoreGridView() {
        return (GridView) findViewById(R.id.store_grid);
    }

    private void setLoadingLayout() {
        getProgressBar().setVisibility(View.VISIBLE);
        getStoreGridView().setVisibility(View.GONE);
        getLinearMap().setVisibility(View.GONE);
        selectStoreButton.setVisibility(View.GONE);
    }

    private void setStoreGridViewHeight(List<Store> result, GridView storeGridView) {
        ViewGroup.LayoutParams params = storeGridView.getLayoutParams();
        int height_size;
        if (result.size() % 2 == 0) {
            height_size = result.size() / 2;
        } else {
            height_size = result.size() / 2 + 1;
        }
        params.height = (int) DensityApi.convertDpToPixel(42 * height_size, this);
        storeGridView.setLayoutParams(params);
    }

    private class TownsTask extends AsyncTask<Void, Void, List<Town>> {

        @Override
        protected List<Town> doInBackground(Void... params) {
            return StoreApi.getTowns(countyId);
        }

        @Override
        protected void onPostExecute(List<Town> result) {
            townArray = result;
            if (townArray != null) {
                ArrayAdapter<Town> townArrayAdapter = new ArrayAdapter<>(
                        SelectDeliverStoreActivity.this, R.layout.myspinner, townArray);
                townArrayAdapter.setDropDownViewResource(R.layout.myspinner);
                townSpinners.setAdapter(townArrayAdapter);
            } else {
                if (NetworkUtil.getConnectivityStatus(SelectDeliverStoreActivity.this) == 0) {
                    Toast.makeText(SelectDeliverStoreActivity.this, "無網路連線", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class RoadsTask extends AsyncTask<Void, Void, List<Road>> {

        @Override
        protected List<Road> doInBackground(Void... params) {
            return StoreApi.getRoads(countyId, townId);
        }

        @Override
        protected void onPostExecute(List<Road> result) {
            roadArray = result;
            if (roadArray != null) {
                ArrayAdapter<Road> roadArrayAdapter = new ArrayAdapter<>(
                        SelectDeliverStoreActivity.this, R.layout.myspinner, roadArray);
                roadArrayAdapter.setDropDownViewResource(R.layout.myspinner);
                roadSpinners.setAdapter(roadArrayAdapter);
            } else {
                if (NetworkUtil.getConnectivityStatus(SelectDeliverStoreActivity.this) == 0) {
                    Toast.makeText(SelectDeliverStoreActivity.this, "無網路連線", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class StoresTask extends AsyncTask<Void, Void, List<Store>> {

        @Override
        protected List<Store> doInBackground(Void... params) {
            return StoreApi.getStores(countyId, townId, road_id);
        }

        @Override
        protected void onPostExecute(final List<Store> result) {
            getProgressBar().setVisibility(View.GONE);
            if (result != null) {
                final StoreGridAdapter storeGridAdapter = new StoreGridAdapter(SelectDeliverStoreActivity.this, result);
                GridView storeGridView = getStoreGridView();
                storeGridView.setAdapter(storeGridAdapter);
                setStoreGridViewHeight(result, storeGridView);
                storeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        updateStoreInfo(result.get(position));
                        storeGridAdapter.updateSelectedPosition(position);
                    }
                });
                storeGridView.setVisibility(View.VISIBLE);
            } else {
                if (NetworkUtil.getConnectivityStatus(SelectDeliverStoreActivity.this) == 0) {
                    Toast.makeText(SelectDeliverStoreActivity.this, "無網路連線", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SelectDeliverStoreActivity.this, "此區無寄送店面", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
