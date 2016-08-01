package com.kosbrother.mongmongwoo.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DensityApi;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductSelectDialogConfirmEvent;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;

import java.util.List;

public class ProductStyleDialog {

    private final Context context;
    private Product product;

    private final AlertDialog alertDialog;
    private final RecyclerView recyclerView;
    private final View countLinearLayout;
    private final TextView countTextView;
    private final TextView itemStockTextView;

    private int tempCount = 1;

    @SuppressLint("InflateParams")
    public ProductStyleDialog(Context context,
                              Product product,
                              final ProductStyleDialogListener listener) {
        this.context = context;
        this.product = product;

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_add_shopping_car_item, null, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.dialog_styles_rv);
        countLinearLayout = view.findViewById(R.id.count_ll);
        countTextView = (TextView) view.findViewById(R.id.count_text_view);
        itemStockTextView = (TextView) view.findViewById(R.id.item_stock_tv);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();

        initMinusButton(view);
        initPlusButton(view);
        initConfirmButton(view, listener);
    }

    public void showWithInitState() {
        initGridView();
        initCountTextView();
        updateSelectedStyle(0);
        updateStyleStock(product.getSpecs().get(0).getStockText());
        alertDialog.show();
    }

    public void showNoCountStyleDialog(Product product) {
        this.product = product;
        tempCount = product.getBuy_count();
        countLinearLayout.setVisibility(View.GONE);
        initGridView();

        Spec selectedSpec = product.getSelectedSpec();
        updateSelectedStyle(getSelectedSpecPosition(product, selectedSpec));
        updateStyleStock(selectedSpec.getStockText());
        alertDialog.show();
    }

    private void initGridView() {
        final int space = (int) DensityApi.convertDpToPixel(1, context);
        recyclerView.addItemDecoration(new SpacesItemDecoration(space));
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
                        params.height += (int) DensityApi.convertDpToPixel(10, context);
                    }
                    recyclerView.setLayoutParams(params);
                }
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector mGestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public void onLongPress(MotionEvent e) {
                        }
                    });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                int position = rv.getChildAdapterPosition(child);
                if (child != null && position != -1 && mGestureDetector.onTouchEvent(e)) {
                    if (position < product.getSpecs().size()) {
                        updateSelectedStyle(position);
                        updateStyleStock(product.getSpecs().get(position).getStockText());
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        SpecsAdapter adapter = new SpecsAdapter(product.getSpecs());
        recyclerView.setAdapter(adapter);
    }

    private void initCountTextView() {
        tempCount = 1;
        updateCountTextView();
    }

    private void initMinusButton(View view) {
        View minusButton = view.findViewById(R.id.minus_button);
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
        View plusButton = view.findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = tempCount + 1;
                updateCountTextView();
            }
        });
    }

    private void initConfirmButton(View view, final ProductStyleDialogListener listener) {
        Button styleConfirmButton = (Button) view.findViewById(R.id.dialog_style_confirm_button);
        styleConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateConfirmProduct();
                GAManager.sendEvent(new ProductSelectDialogConfirmEvent(product.getName()));
                GAManager.sendShoppingCartProductEvent(product);
                checkFirstAddAndNotifyListener(listener);
                alertDialog.dismiss();
            }
        });
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

    private void updateStyleStock(String stock) {
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

    private void checkFirstAddAndNotifyListener(ProductStyleDialogListener listener) {
        if (Settings.checkIsFirstAddShoppingCar()) {
            Settings.setKownShoppingCar();
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

        public SpecsAdapter(List<Spec> specs) {
            this.specs = specs;
            selectedPosition = 0;
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

            // If position >= specs size, add black view
            if (position >= specs.size()) {
                storeText.setText("");
                imageView.setImageResource(0);
            } else {
                Spec spec = specs.get(position);
                storeText.setText(spec.getStyle());
                Glide.with(context)
                        .load(spec.getStylePic().getUrl())
                        .centerCrop()
                        .placeholder(R.mipmap.img_pre_load_square)
                        .into(imageView);
            }

            if (position == selectedPosition) {
                itemView.setBackgroundResource(R.drawable.bg_spec_selected);
                storeText.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                itemView.setBackgroundResource(R.drawable.bg_spec_non_selected);
                storeText.setTextColor(ContextCompat.getColor(context, R.color.black_text));
            }
        }

        @Override
        public int getItemCount() {
            // If size % 3 != 0, add blank item to fill recyclerView
            int size = specs.size();
            if (size % 3 == 0) {
                return size;
            }
            return size + (3 - size % 3);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView storeText;
            private final ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.storeText = (TextView) itemView.findViewById(R.id.item_spec_tv);
                this.imageView = (ImageView) itemView.findViewById(R.id.item_spec_iv);
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
            if (parent.getChildAdapterPosition(view) < 4) {
                outRect.top = space;
            }
            outRect.right = space;
            outRect.bottom = space;
        }
    }

}
