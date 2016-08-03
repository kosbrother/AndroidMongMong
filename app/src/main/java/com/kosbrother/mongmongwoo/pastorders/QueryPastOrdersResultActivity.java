package com.kosbrother.mongmongwoo.pastorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.PastOrdersAdapter;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.entity.postorder.PostOrder;
import com.kosbrother.mongmongwoo.fragments.CsBottomSheetDialogFragment;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.customerservice.CustomerServiceClickEvent;

import java.util.List;

import rx.functions.Action1;

public class QueryPastOrdersResultActivity extends AppCompatActivity {

    public static final String EXTRA_STRING_EMAIL = "EXTRA_STRING_EMAIL";
    public static final String EXTRA_STRING_PHONE = "EXTRA_STRING_PHONE";

    private CsBottomSheetDialogFragment csBottomSheetDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        setToolbar();
        requestOrders();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @SuppressWarnings("ConstantConditions")
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void requestOrders() {
        Intent intent = getIntent();
        String email = intent.getStringExtra(EXTRA_STRING_EMAIL);
        String phone = intent.getStringExtra(EXTRA_STRING_PHONE);
        Webservice.getOrdersByEmailAndPhone(email, phone,
                new Action1<ResponseEntity<List<PostOrder>>>() {
                    @Override
                    public void call(ResponseEntity<List<PostOrder>> listResponseEntity) {
                        List<PostOrder> data = listResponseEntity.getData();
                        if (data == null) {
                            GAManager.sendError("getOrdersByEmailAndPhoneError", listResponseEntity.getError());
                        } else {
                            onGetOrdersResult(data);
                        }
                    }
                }
        );

    }

    private void onGetOrdersResult(List<PostOrder> postOrders) {
        if (postOrders.size() == 0) {
            setContentView(R.layout.activity_query_past_orders_result_empty);
            setToolbar();
            initCsBottomSheet();
            return;
        }
        setContentView(R.layout.activity_query_past_orders_result);
        setToolbar();
        initCsBottomSheet();
        setGridView(postOrders);
    }

    private void initCsBottomSheet() {
        csBottomSheetDialogFragment = new CsBottomSheetDialogFragment();
    }

    private void setGridView(final List<PostOrder> postOrders) {
        GridView ordersGridView = (GridView) findViewById(R.id.orders_gv);
        assert ordersGridView != null;
        ordersGridView.setAdapter(new PastOrdersAdapter(this, postOrders));
        ordersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onOrderItemClick(postOrders.get(position).getId());
            }
        });
    }

    private void onOrderItemClick(int orderId) {
        Intent intent = new Intent(this, PastOrderDetailActivity.class);
        intent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID, orderId);
        startActivity(intent);
    }

    public void onCustomerServiceFabClick(View view) {
        csBottomSheetDialogFragment.show(getSupportFragmentManager(), "");
        GAManager.sendEvent(new CustomerServiceClickEvent("FAB"));
    }
}
