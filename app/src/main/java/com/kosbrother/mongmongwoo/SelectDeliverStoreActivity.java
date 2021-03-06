package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kosbrother.mongmongwoo.adpters.StoreGridAdapter;
import com.kosbrother.mongmongwoo.api.DensityApi;
import com.kosbrother.mongmongwoo.api.StoreApi;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.model.County;
import com.kosbrother.mongmongwoo.model.Road;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.model.Town;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;

import java.util.List;

import rx.functions.Action1;

public class SelectDeliverStoreActivity extends BaseActivity implements
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener {

    public static final String EXTRA_SELECTED_STORE = "EXTRA_SELECTED_STORE";
    private ScrollView scrollView;
    private GoogleMap mMap;

    private Spinner townSpinners;
    private Spinner roadSpinners;
    private Button selectStoreButton;

    private int county_id;
    private int town_id;
    private int road_id;
    private Store selectedStore;

    private List<County> counties;
    private List<Town> townArray;
    private List<Road> roadArray;
    private Marker marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        counties = StoreApi.getCounties();
        setContentView(R.layout.activity_select_deliver_store);
        scrollView = (ScrollView) findViewById(R.id.activity_select_deliver_store_sv);
        setToolbar();
        setMapFragment();
        setMapTouchView();
        setCountySpinner();
        setTownSpinner();
        setRoadSpinner();
        setSelectStoreButton();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
    }

    public void updateStoreInfo(Store theStore) {
        selectedStore = theStore;

        TextView storeName = (TextView) findViewById(R.id.select_store_name);
        storeName.setText(selectedStore.getName());
        TextView storeAddress = (TextView) findViewById(R.id.select_store_address);
        storeAddress.setText(selectedStore.getAddress());

        getLinearMap().setVisibility(View.VISIBLE);
        selectStoreButton.setVisibility(View.VISIBLE);

        if (mMap != null) {
            if (marker != null) {
                marker.remove();
                marker = null;
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(theStore.getLatLng()));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        }
    }

    private void setMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setMapTouchView() {
        View view = findViewById(R.id.activity_select_deliver_store_map_touch_view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_UP:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    default:
                        return true;
                }
            }
        });
    }

    private void setCountySpinner() {
        Spinner countySpinners = (Spinner) findViewById(R.id.country_spinner);
        ArrayAdapter<County> countyArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_drop_down_arrow, counties);
        countyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpinners.setAdapter(countyArrayAdapter);

        countySpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                county_id = counties.get(position).getId();
                setLoadingLayout();
                Webservice.getTowns(county_id, new Action1<ResponseEntity<List<Town>>>() {
                    @Override
                    public void call(ResponseEntity<List<Town>> listResponseEntity) {
                        List<Town> data = listResponseEntity.getData();
                        if (data == null) {
                            GAManager.sendError("getTownsError", listResponseEntity.getError());
                            if (NetworkUtil.getConnectivityStatus(SelectDeliverStoreActivity.this) == 0) {
                                Toast.makeText(SelectDeliverStoreActivity.this, "無網路連線", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            townArray = data;
                            ArrayAdapter<Town> townArrayAdapter = new ArrayAdapter<>(
                                    SelectDeliverStoreActivity.this, R.layout.spinner_drop_down_arrow, townArray);
                            townArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            townSpinners.setAdapter(townArrayAdapter);
                        }
                    }
                });
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
                    town_id = townArray.get(position).getId();
                    setLoadingLayout();
                    Webservice.getRoads(county_id, town_id, new Action1<ResponseEntity<List<Road>>>() {
                        @Override
                        public void call(ResponseEntity<List<Road>> listResponseEntity) {
                            List<Road> data = listResponseEntity.getData();
                            if (data == null) {
                                GAManager.sendError("getRoadsError", listResponseEntity.getError());
                                if (NetworkUtil.getConnectivityStatus(SelectDeliverStoreActivity.this) == 0) {
                                    Toast.makeText(SelectDeliverStoreActivity.this, "無網路連線", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                roadArray = data;
                                ArrayAdapter<Road> roadArrayAdapter = new ArrayAdapter<>(
                                        SelectDeliverStoreActivity.this, R.layout.spinner_drop_down_arrow, roadArray);
                                roadArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                roadSpinners.setAdapter(roadArrayAdapter);
                            }
                        }
                    });
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
                    Webservice.getStores(county_id, town_id, road_id, new Action1<ResponseEntity<List<Store>>>() {
                        @Override
                        public void call(ResponseEntity<List<Store>> listResponseEntity) {
                            final List<Store> data = listResponseEntity.getData();
                            if (data == null) {
                                GAManager.sendError("getStoresError", listResponseEntity.getError());
                                if (NetworkUtil.getConnectivityStatus(SelectDeliverStoreActivity.this) == 0) {
                                    Toast.makeText(SelectDeliverStoreActivity.this, "無網路連線", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                getProgressBar().setVisibility(View.GONE);
                                if (data.size() == 0) {
                                    Toast.makeText(SelectDeliverStoreActivity.this, "此區無寄送店面", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                final StoreGridAdapter storeGridAdapter = new StoreGridAdapter(SelectDeliverStoreActivity.this, data);
                                GridView storeGridView = getStoreGridView();
                                storeGridView.setAdapter(storeGridAdapter);
                                setStoreGridViewHeight(data, storeGridView);
                                storeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        updateStoreInfo(data.get(position));
                                        storeGridAdapter.updateSelectedPosition(position);
                                    }
                                });
                                storeGridView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
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
                intent.putExtra(EXTRA_SELECTED_STORE, selectedStore);
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

    @Override
    public void onCameraIdle() {
        if (marker != null) {
            return;
        }
        if (mMap != null && selectedStore != null) {
            marker = mMap.addMarker(new MarkerOptions()
                    .title(selectedStore.getName())
                    .position(selectedStore.getLatLng())
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_7_11)));

        }
    }

}
