package com.kosbrother.mongmongwoo.pastorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.PastOrdersAdapter;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.postorder.PostOrder;
import com.kosbrother.mongmongwoo.fragments.CsBottomSheetDialogFragment;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.customerservice.CustomerServiceClickEvent;

import java.util.List;

public class QueryPastOrdersResultActivity extends BaseActivity implements DataManager.ApiCallBack {

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
    protected void onDestroy() {
        DataManager.getInstance().unSubscribe(this);
        super.onDestroy();
    }

    public void onCustomerServiceFabClick(View view) {
        csBottomSheetDialogFragment.show(getSupportFragmentManager(), "");
        GAManager.sendEvent(new CustomerServiceClickEvent("FAB"));
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(Object data) {
        onGetOrdersResult((List<PostOrder>) data);
    }

    private void onOrderItemClick(int orderId) {
        Intent intent = new Intent(this, PastOrderDetailActivity.class);
        intent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID, orderId);
        startActivity(intent);
    }

    private void requestOrders() {
        Intent intent = getIntent();
        String email = intent.getStringExtra(EXTRA_STRING_EMAIL);
        String phone = intent.getStringExtra(EXTRA_STRING_PHONE);
        DataManager.getInstance().getOrdersByEmailAndPhone(email, phone, this);
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
}
