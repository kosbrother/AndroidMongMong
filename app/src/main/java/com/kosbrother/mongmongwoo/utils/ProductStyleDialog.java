package com.kosbrother.mongmongwoo.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.api.DensityApi;
import com.kosbrother.mongmongwoo.entity.mycollect.PostWishListsEntity;
import com.kosbrother.mongmongwoo.facebookevent.FacebookLogger;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductSelectDialogConfirmEvent;
import com.kosbrother.mongmongwoo.login.LoginActivity;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;
import com.kosbrother.mongmongwoo.widget.RecyclerViewOnItemTouchListener;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class ProductStyleDialog {

    private final Context context;
    private Product product;
    private ProductStyleDialogListener listener;

    private final AlertDialog alertDialog;
    private final RecyclerView recyclerView;
    private final View countLinearLayout;
    private final TextView countTextView;
    private final TextView itemStockTextView;
    private final Button confirmButton;
    private final View noStockHintTextView;
    private SpacesItemDecoration decoration;

    private int tempCount = 1;
    private View.OnTouchListener onImageViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    ImageView imageView = (ImageView) view;
                    imageView.getDrawable().setColorFilter(
                            ContextCompat.getColor(context, R.color.bg_green_color_filter), PorterDuff.Mode.SRC_ATOP);
                    imageView.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    ImageView imageView = (ImageView) view;
                    imageView.getDrawable().clearColorFilter();
                    view.invalidate();
                    break;
                }
            }
            return false;
        }
    };
    private View.OnClickListener onConfirmClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateConfirmProduct();
            GAManager.sendEvent(new ProductSelectDialogConfirmEvent(product.getName()));
            GAManager.sendShoppingCartProductEvent(product);
            FacebookLogger.getInstance().logAddedToCartEvent(
                    String.valueOf(product.getId()),
                    product.getCategoryName(),
                    product.getName(),
                    product.getFinalPrice()
            );
            checkFirstAddAndNotifyListener();
            alertDialog.dismiss();
        }
    };

    private View.OnClickListener onAddToWishClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Settings.checkIsLogIn()) {
                updateConfirmProduct();
                GAManager.sendEvent(new ProductSelectDialogConfirmEvent(product.getName()));
                addToWishList();
            } else {
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        }
    };

    @SuppressLint("InflateParams")
    public ProductStyleDialog(Context context,
                              Product product,
                              final ProductStyleDialogListener listener) {
        this.context = context;
        this.product = product;
        this.listener = listener;

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_product_style, null, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.dialog_product_style_rv);
        countLinearLayout = view.findViewById(R.id.dialog_product_style_count_ll);
        countTextView = (TextView) view.findViewById(R.id.dialog_product_style_count_tv);
        itemStockTextView = (TextView) view.findViewById(R.id.dialog_product_style_stock_tv);
        confirmButton = (Button) view.findViewById(R.id.dialog_product_style_confirm_btn);
        noStockHintTextView = view.findViewById(R.id.dialog_product_style_no_stock_hint_tv);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();

        initMinusButton(view);
        initPlusButton(view);
    }

    public void showWithInitState() {
        List<Spec> specs = product.getSpecs();
        int initSelectedPosition = getInitSelectedPosition(specs);

        initGridView(initSelectedPosition);
        updateStock(specs.get(initSelectedPosition).getStockText());
        updateConfirmButtonAndNoStockHint(specs.get(initSelectedPosition).getStockAmount());
        initCountTextView();

        alertDialog.show();
    }

    public void showNoCountStyleDialog(Product product) {
        this.product = product;
        tempCount = product.getBuy_count();
        countLinearLayout.setVisibility(View.GONE);

        Spec selectedSpec = product.getSelectedSpec();
        initGridView(getSelectedSpecPosition(product, selectedSpec));
        updateStock(selectedSpec.getStockText());
        updateConfirmButtonAndNoStockHint(selectedSpec.getStockAmount());

        alertDialog.show();
    }

    private void initGridView(int selectedPosition) {
        final int space = (int) DensityApi.convertDpToPixel(1, context);
        if (decoration == null) {
            decoration = new SpacesItemDecoration(space);
            recyclerView.addItemDecoration(decoration);
        }
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                int specSize = product.getSpecs().size();
                if (specSize > 3) {
                    View child1 = recyclerView.getChildAt(0);
                    View child4 = recyclerView.getChildAt(4);
                    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();

                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                    params.height = manager.getDecoratedMeasuredHeight(child1) +
                            manager.getDecoratedMeasuredHeight(child4);
                    if (specSize > 6) {
                        params.height += (int) DensityApi.convertDpToPixel(40, context);
                    }
                    recyclerView.setLayoutParams(params);
                }
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerViewOnItemTouchListener(context,
                new Action1<Integer>() {
                    @Override
                    public void call(Integer position) {
                        if (position < product.getSpecs().size()) {
                            updateSelectedStyle(position);
                            Spec spec = product.getSpecs().get(position);
                            updateStock(spec.getStockText());
                            updateConfirmButtonAndNoStockHint(spec.getStockAmount());
                        }
                    }
                }));
        SpecsAdapter adapter = new SpecsAdapter(product.getSpecs(), selectedPosition);
        recyclerView.setAdapter(adapter);
    }

    private void updateConfirmButtonAndNoStockHint(int stockAmount) {
        if (stockAmount == 0) {
            confirmButton.setText("加入收藏，貨到通知我");
            confirmButton.setOnClickListener(onAddToWishClickListener);
            noStockHintTextView.setVisibility(View.VISIBLE);
        } else {
            confirmButton.setText("確定");
            confirmButton.setOnClickListener(onConfirmClickListener);
            noStockHintTextView.setVisibility(View.GONE);
        }
    }

    private void addToWishList() {
        int userId = Settings.getSavedUser().getUserId();
        ArrayList<PostWishListsEntity.WishItemEntity> wishLists = new ArrayList<>();
        wishLists.add(new PostWishListsEntity.WishItemEntity(product.getId(), product.getSelectedSpec().getId()));
        PostWishListsEntity entity = new PostWishListsEntity(wishLists);
        DataManager.getInstance().postWishLists(userId, entity, new DataManager.ApiCallBack() {
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }

            @Override
            public void onSuccess(Object data) {
                Toast.makeText(context, "成功加入補貨清單", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

    private void initCountTextView() {
        tempCount = 1;
        updateCountTextView();
    }

    private void initMinusButton(View view) {
        View minusButton = view.findViewById(R.id.dialog_product_style_minus_button);
        minusButton.setOnTouchListener(onImageViewTouchListener);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempCount != 1) {
                    tempCount = tempCount - 1;
                    updateCountTextView();
                }
            }
        });
    }

    private void initPlusButton(View view) {
        View plusButton = view.findViewById(R.id.dialog_product_style_plus_button);
        plusButton.setOnTouchListener(onImageViewTouchListener);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = tempCount + 1;
                updateCountTextView();
            }
        });
    }

    private int getInitSelectedPosition(List<Spec> specs) {
        int selectedPosition = 0;
        for (int i = 0; i < specs.size(); i++) {
            Spec spec = specs.get(i);
            if (spec.getStockAmount() > 0) {
                selectedPosition = i;
                break;
            }
        }
        return selectedPosition;
    }

    private int getSelectedSpecPosition(Product product, Spec selectedSpec) {
        List<Spec> specs = product.getSpecs();
        int position = 0;
        for (int i = 0; i < specs.size(); i++) {
            if (specs.get(i).getId() == selectedSpec.getId()) {
                return i;
            }
        }
        return position;
    }

    private void updateSelectedStyle(int position) {
        SpecsAdapter adapter = (SpecsAdapter) recyclerView.getAdapter();
        adapter.updateSelectedPosition(position);
    }

    private void updateStock(String stock) {
        itemStockTextView.setText(stock);
    }

    private void updateCountTextView() {
        countTextView.setText(String.valueOf(tempCount));
    }

    private void updateConfirmProduct() {
        SpecsAdapter adapter = (SpecsAdapter) recyclerView.getAdapter();
        Spec selectedSpec = product.getSpecs().get(adapter.getSelectedPosition());
        product.setSelectedSpec(selectedSpec);
        product.setBuy_count(tempCount);
    }

    private void checkFirstAddAndNotifyListener() {
        if (Settings.checkIsFirstAddShoppingCar()) {
            Settings.setKnowShoppingCar();
            listener.onFirstAddShoppingCart();
        }
        listener.onConfirmButtonClick(product);
    }

    public interface ProductStyleDialogListener {

        void onFirstAddShoppingCart();

        void onConfirmButtonClick(Product product);

    }

    private static class SpecsAdapter extends RecyclerView.Adapter<SpecsAdapter.ViewHolder> {
        private final List<Spec> specs;
        private int selectedPosition;

        private Context context;

        public SpecsAdapter(List<Spec> specs, int selectedPosition) {
            this.specs = specs;
            this.selectedPosition = selectedPosition;
        }

        public void updateSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            context = parent.getContext();
            View itemView = LayoutInflater.from(context).
                    inflate(R.layout.item_spec, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            View itemView = holder.itemView;
            TextView storeText = holder.storeText;
            ImageView imageView = holder.imageView;
            View noStockDiagonalView = holder.noStockDiagonalView;

            // If position >= specs size, set empty view
            if (position >= specs.size()) {
                storeText.setText("");
                imageView.setImageResource(0);
                noStockDiagonalView.setVisibility(View.GONE);
            } else {
                Spec spec = specs.get(position);
                storeText.setText(spec.getStyle());
                Glide.with(context)
                        .load(spec.getStylePic().getUrl())
                        .centerCrop()
                        .placeholder(R.mipmap.img_pre_load_square)
                        .into(imageView);

                if (position == selectedPosition) {
                    itemView.setBackgroundResource(R.drawable.bg_spec_selected);
                    storeText.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    itemView.setBackgroundResource(R.drawable.bg_spec_non_selected);
                    storeText.setTextColor(ContextCompat.getColor(context, R.color.black_4a4a4a));
                }

                int stockAmount = spec.getStockAmount();
                noStockDiagonalView.setVisibility(stockAmount == 0 ? View.VISIBLE : View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            // If size % 3 != 0, add empty view to fill recyclerView
            int size = specs.size();
            if (size % 3 == 0) {
                return size;
            }
            return size + (3 - size % 3);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView storeText;
            private final ImageView imageView;
            private final View noStockDiagonalView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.storeText = (TextView) itemView.findViewById(R.id.item_spec_tv);
                this.imageView = (ImageView) itemView.findViewById(R.id.item_spec_iv);
                this.noStockDiagonalView = itemView.findViewById(R.id.item_spec_no_stock_diagonal_view);
            }

        }
    }

    private static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) < 3) {
                outRect.top = space;
            }
            outRect.right = space;
            outRect.bottom = space;
        }
    }

}
