package com.kosbrother.mongmongwoo.mycollect;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.ProductActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.WishListAdapter;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.model.Category;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;
import com.kosbrother.mongmongwoo.widget.CenterProgressDialog;

import java.util.List;

public class WishListFragment extends Fragment implements WishListView, WishListAdapter.WishListListener {

    private WishListPresenter presenter;
    private FrameLayout container;
    private CenterProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new WishListPresenter(this, new WishListModel(DataManager.getInstance()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wish_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        container = (FrameLayout) view.findViewById(R.id.fragment_wish_list_container_fl);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void showLoadingView() {
        container.removeAllViews();
        View loadingView = LayoutInflater.from(getContext()).inflate(R.layout.loading_no_toolbar, null);
        container.addView(loadingView);
    }

    @Override
    public void showWishListView(List<Product> wishListProducts, List<Spec> specs) {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        WishListAdapter adapter = new WishListAdapter(wishListProducts, specs, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        container.removeAllViews();
        container.addView(recyclerView);
    }

    @Override
    public void showWishListEmptyView() {
        container.removeAllViews();
        View loadingView = LayoutInflater.from(getContext()).inflate(R.layout.wish_list_empty, null);
        container.addView(loadingView);
    }

    @Override
    public void startProductActivity(int productId) {
        Intent intent = new Intent(getActivity(), ProductActivity.class);
        intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, productId);
        intent.putExtra(ProductActivity.EXTRA_INT_CATEGORY_ID, Category.Type.ALL.getId());
        intent.putExtra(ProductActivity.EXTRA_STRING_CATEGORY_NAME, Category.Type.ALL.getName());
        startActivity(intent);
    }

    @Override
    public void showProgressDialog() {
        progressDialog = CenterProgressDialog.show(getContext());
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showToast(String errorMessage) {
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWishItemClick(int position) {
        presenter.onWishItemClick(position);
    }

    @Override
    public void onDeleteWishItemClick(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("取消補貨");
        alertDialogBuilder.setMessage("是否確定要取消補貨");
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.onConfirmDeleteWishItemClick(position);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
