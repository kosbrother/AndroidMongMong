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

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.model.Category;
import com.kosbrother.mongmongwoo.product.ProductActivity;
import com.kosbrother.mongmongwoo.widget.CenterProgressDialog;

import java.util.List;

public class FavoriteFragment extends Fragment implements FavoriteView, FavoriteListener {

    private FavoritePresenter presenter;
    private FrameLayout container;
    private CenterProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int userId = Settings.getSavedUser().getUserId();
        FavoriteManager favoriteManager =
                FavoriteManager.getInstance(userId, getActivity().getApplicationContext());
        presenter = new FavoritePresenter(this, favoriteManager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        container = (FrameLayout) view.findViewById(R.id.fragment_favorite_container_fl);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onCollectItemClick(int productId) {
        presenter.onCollectItemClick(productId);
    }

    @Override
    public void onCancelCollectClick(final int productId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("取消收藏");
        alertDialogBuilder.setMessage("是否確定要取消收藏");
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.onCancelCollectConfirmClick(productId);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void showLoadingView() {
        container.removeAllViews();
        container.addView(LayoutInflater.from(getContext()).inflate(R.layout.loading_no_toolbar, null));
    }

    @Override
    public void showEmptyView() {
        container.removeAllViews();
        container.addView(LayoutInflater.from(getContext()).inflate(R.layout.favorite_empty, null));
    }

    @Override
    public void showMyCollectList(List<FavoriteViewModel> favoriteViewModels) {
        RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        FavoriteAdapter adapter = new FavoriteAdapter(favoriteViewModels, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        container.removeAllViews();
        container.addView(recyclerView);
    }

    @Override
    public void showProgressDialog() {
        progressDialog = CenterProgressDialog.show(getContext());
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
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showToast(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
