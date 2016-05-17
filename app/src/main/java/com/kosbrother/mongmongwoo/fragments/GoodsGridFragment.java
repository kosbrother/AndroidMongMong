package com.kosbrother.mongmongwoo.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.ProductActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.ShoppingCarPreference;
import com.kosbrother.mongmongwoo.adpters.GoodsGridAdapter;
import com.kosbrother.mongmongwoo.adpters.StyleGridAdapter;
import com.kosbrother.mongmongwoo.api.ProductApi;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.indexgridcart.IndexGridCartAddToCartEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductSelectDialogConfirmEvent;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;
import com.kosbrother.mongmongwoo.utils.EndlessScrollListener;
import com.kosbrother.mongmongwoo.utils.MaxHeightGridView;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;

import java.util.ArrayList;

public class GoodsGridFragment extends Fragment implements GoodsGridAdapter.GoodsGridAdapterListener {

    private GridView mGridView;
    private GoodsGridAdapter goodsGridAdapter;
    private RelativeLayout layoutProgress;

    private ArrayList<Product> products = new ArrayList<>();
    private int category_id;
    public static final String ARG_CATEGORY = "CATEGORY_ID";

    private int page = 1;

    private int tempCount;
    private StyleGridAdapter styleGridAdapter;
    private ArrayList<Spec> specs;

    private MaxHeightGridView styleGrid;
    private ImageView styleImage;
    private TextView styleName;


    public static GoodsGridFragment newInstance(int category_id) {
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY, category_id);
        GoodsGridFragment fragment = new GoodsGridFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category_id = getArguments().getInt(ARG_CATEGORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        mGridView = (GridView) view.findViewById(R.id.fragment_gridview);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = products.get(position);
                Intent intent = new Intent(getContext(), ProductActivity.class);
                intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, product.getId());
                getContext().startActivity(intent);
            }
        });
        layoutProgress = (RelativeLayout) view.findViewById(R.id.layout_progress);

        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                new NewsTask().execute();
            }
        });

        if (goodsGridAdapter != null) {
            layoutProgress.setVisibility(View.GONE);
            mGridView.setAdapter(goodsGridAdapter);
        } else {
            layoutProgress.setVisibility(View.VISIBLE);
            new NewsTask().execute();
        }
        return view;
    }

    @Override
    public void onAddShoppingCartButtonClick(int productId, int position) {
        GAManager.sendEvent(new IndexGridCartAddToCartEvent(products.get(position).getName()));
        if (NetworkUtil.getConnectivityStatus(getContext()) != 0) {
            showStyleDialog(position);
            new GetProductSpectsTask().execute(productId);
        } else {
            Toast.makeText(getContext(), "無法取得資料,請檢查網路連線", Toast.LENGTH_SHORT).show();
        }
    }

    public void showStyleDialog(final int position) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_shopping_car_item, null, false);
        styleGrid = (MaxHeightGridView) view.findViewById(R.id.dialog_styles_gridview);
        styleImage = (ImageView) view.findViewById(R.id.dialog_style_image);
        styleName = (TextView) view.findViewById(R.id.dialog_style_name);

        Button style_confirm_button = (Button) view.findViewById(R.id.dialog_style_confirm_button);

        Button minusButton = (Button) view.findViewById(R.id.minus_button);
        Button plusButton = (Button) view.findViewById(R.id.plus_button);
        final TextView countText = (TextView) view.findViewById(R.id.count_text_view);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempCount != 1) {
                    tempCount = tempCount - 1;
                    countText.setText(String.valueOf(tempCount));
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = tempCount + 1;
                countText.setText(String.valueOf(tempCount));
            }
        });
        tempCount = 1;
        countText.setText(String.valueOf(tempCount));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(view);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();

        style_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product theProduct = products.get(position);
                GAManager.sendEvent(new ProductSelectDialogConfirmEvent(theProduct.getName()));

                int selectedStylePosition = styleGridAdapter.getSelectedPosition();
                Spec theSelectedSpec = specs.get(selectedStylePosition);
                theProduct.setSelectedSpec(theSelectedSpec);
                ShoppingCarPreference pref = new ShoppingCarPreference();
                theProduct.setBuy_count(tempCount);
                pref.addShoppingItem(getContext(), theProduct);
                ((MainActivity) getActivity()).doIncrease();
                alertDialog.cancel();

                if (Settings.checkIsFirstAddShoppingCar()) {
                    ((MainActivity) getActivity()).showShoppingCarInstruction();
                    Settings.setKownShoppingCar();
                }

            }
        });

    }

    private class NewsTask extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] params) {
            ArrayList<Product> feedBackProducts = ProductApi.getCategoryProducts(category_id, page);
            if (feedBackProducts != null && feedBackProducts.size() > 0) {
                products.addAll(feedBackProducts);
                page = page + 1;
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            layoutProgress.setVisibility(View.GONE);
            if ((boolean) result) {
                if (goodsGridAdapter == null) {
                    MainActivity activity = (MainActivity) getActivity();
                    goodsGridAdapter = new GoodsGridAdapter(activity, products, GoodsGridFragment.this);
                    mGridView.setAdapter(goodsGridAdapter);
                } else {
                    goodsGridAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public int getProductsSize() {
        if (products != null) {
            return products.size();
        } else {
            return 0;
        }
    }

    private class GetProductSpectsTask extends AsyncTask<Integer, Void, ArrayList<Spec>> {

        @Override
        protected ArrayList<Spec> doInBackground(Integer... params) {
            return ProductApi.getProductSpects(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Spec> result) {
            specs = result;
            if (specs != null && specs.size() != 0) {
                styleGridAdapter = new StyleGridAdapter(getActivity(), specs);
                styleGrid.setAdapter(styleGridAdapter);
                styleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Spec spec = GoodsGridFragment.this.specs.get(position);

                        Glide.with(getContext())
                                .load(spec.getPic())
                                .centerCrop()
                                .placeholder(R.mipmap.img_pre_load_square)
                                .into(styleImage);
                        styleName.setText(spec.getStyle());

                        styleGridAdapter.updateSelectedPosition(position);
                    }
                });

                Glide.with(getContext())
                        .load(specs.get(0).getPic())
                        .centerCrop()
                        .placeholder(R.mipmap.img_pre_load_square)
                        .into(styleImage);
                styleName.setText(specs.get(0).getStyle());

            } else {
                Toast.makeText(getContext(), "無法取得資料,請檢查網路連線", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
