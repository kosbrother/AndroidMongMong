package com.kosbrother.mongmongwoo.fragments;

import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.CustomerServiceAdapter;
import com.kosbrother.mongmongwoo.utils.CustomerServiceUtil;

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
                    public void onLineCustomerServiceClick() {
                        CustomerServiceUtil.startToLineService(getContext());
                    }

                    @Override
                    public void onFacebookCustomerServiceClick() {
                        CustomerServiceUtil.startToFbService(getContext());
                    }
                }));
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (!isAdded()) {
            super.show(manager, tag);
        }else {
            FragmentTransaction ft = manager.beginTransaction();
            ft.commit();
        }
    }
}
