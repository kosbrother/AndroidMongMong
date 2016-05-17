package com.kosbrother.mongmongwoo.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.CustomerServiceAdapter;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.customerservice.CustomerServiceClickEvent;

public class CsBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_customer_service, null);
        dialog.setContentView(contentView);

        RecyclerView mRecyclerView = (RecyclerView) contentView.findViewById(R.id.bottom_sheet_customer_service_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new CustomerServiceAdapter(
                new CustomerServiceAdapter.OnCustomerServiceItemClickListener() {
                    @Override
                    public void onLineCustomerServiceClick(String text) {
                        GAManager.sendEvent(new CustomerServiceClickEvent(text));
                        actionToUrl("http://line.me/ti/p/%40kya5456n");
                    }

                    @Override
                    public void onFacebookCustomerServiceClick(String text) {
                        GAManager.sendEvent(new CustomerServiceClickEvent(text));
                        actionToUrl("https://www.facebook.com/kingofgametw/");
                    }
                }));
    }

    private void actionToUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
