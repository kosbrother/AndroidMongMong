package com.kosbrother.mongmongwoo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.ProductActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.GoodsGridAdapter;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.indexgridcart.IndexGridCartAddToCartEvent;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.EndlessScrollListener;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class GoodsGridFragment extends Fragment implements GoodsGridAdapter.GoodsGridAdapterListener {

    private static final String ARG_CATEGORY_ID = "CATEGORY_ID";
    private static final String ARG_CATEGORY_NAME = "ARG_CATEGORY_NAME";

    private GridView mGridView;
    private GoodsGridAdapter goodsGridAdapter;
    private RelativeLayout layoutProgress;

    private List<Product> products = new ArrayList<>();
    private int categoryId;
    private String categoryName;

    private int page = 1;
    private ProductStyleDialog dialog;

    public static GoodsGridFragment newInstance(int categoryId, String categoryName) {
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        GoodsGridFragment fragment = new GoodsGridFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        mGridView = (GridView) view.findViewById(R.id.fragment_gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = products.get(position);
                Intent intent = new Intent(getContext(), ProductActivity.class);
                intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, product.getId());
                intent.putExtra(ProductActivity.EXTRA_INT_CATEGORY_ID, categoryId);
                intent.putExtra(ProductActivity.EXTRA_STRING_CATEGORY_NAME, categoryName);
                getContext().startActivity(intent);
            }
        });
        layoutProgress = (RelativeLayout) view.findViewById(R.id.layout_progress);

        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getProducts();
            }
        });

        if (goodsGridAdapter != null) {
            layoutProgress.setVisibility(View.GONE);
            mGridView.setAdapter(goodsGridAdapter);
        } else {
            layoutProgress.setVisibility(View.VISIBLE);
            getProducts();
        }
        return view;
    }

    private void getProducts() {
        Webservice.getProducts(categoryId, page, new Action1<ResponseEntity<List<Product>>>() {
            @Override
            public void call(ResponseEntity<List<Product>> listResponseEntity) {
                List<Product> feedBackProducts = listResponseEntity.getData();
                if (feedBackProducts == null) {
                    GAManager.sendError("getProductsError", listResponseEntity.getError());
                } else {
                    products.addAll(feedBackProducts);
                    page = page + 1;
                    layoutProgress.setVisibility(View.GONE);
                    if (goodsGridAdapter == null) {
                        MainActivity activity = (MainActivity) getActivity();
                        goodsGridAdapter = new GoodsGridAdapter(activity, products, GoodsGridFragment.this);
                        mGridView.setAdapter(goodsGridAdapter);
                    } else {
                        goodsGridAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }

    @Override
    public void onAddShoppingCartButtonClick(int productId, int position) {
        Product product = products.get(position);
        GAManager.sendEvent(new IndexGridCartAddToCartEvent(product.getName()));
        if (dialog == null) {
            dialog = new ProductStyleDialog(getContext(), product, new ProductStyleDialog.ProductStyleDialogListener() {
                @Override
                public void onFirstAddShoppingCart() {
                    ((MainActivity) getActivity()).showShoppingCarInstruction();
                }

                @Override
                public void onConfirmButtonClick() {
                    getActivity().invalidateOptionsMenu();
                    Toast.makeText(getContext(), "成功加入購物車", Toast.LENGTH_SHORT).show();
                }
            });
        }
        dialog.showWithInitState(product);
    }

}
