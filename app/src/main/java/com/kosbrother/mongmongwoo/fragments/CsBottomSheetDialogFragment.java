package com.kosbrother.mongmongwoo.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kosbrother.mongmongwoo.AnalyticsApplication;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.CustomerServiceAdapter;

public class CsBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final Tracker tracker = ((AnalyticsApplication) getActivity().getApplicationContext())
                .getDefaultTracker();

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_customer_service, null);
        dialog.setContentView(contentView);

        RecyclerView mRecyclerView = (RecyclerView) contentView.findViewById(R.id.bottom_sheet_customer_service_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new CustomerServiceAdapter(
                new CustomerServiceAdapter.OnCustomerServiceItemClickListener() {
                    @Override
                    public void onLineCustomerServiceClick(String text) {
                        actionToUrl("http://line.me/ti/p/%40kya5456n");
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("CUSTOMER_SERVICE")
                                .setAction("CLICK")
                                .setLabel(text)
                                .build());
                    }

                    @Override
                    public void onFacebookCustomerServiceClick(String text) {
                        actionToUrl("https://www.facebook.com/kingofgametw/");
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("CUSTOMER_SERVICE")
                                .setAction("CLICK")
                                .setLabel(text)
                                .build());
                    }
                }));
    }

    private void actionToUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
