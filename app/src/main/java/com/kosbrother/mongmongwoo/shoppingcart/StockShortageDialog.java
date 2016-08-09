package com.kosbrother.mongmongwoo.shoppingcart;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DensityApi;
import com.kosbrother.mongmongwoo.entity.postorder.UnableToBuyModel;
import com.kosbrother.mongmongwoo.login.BaseNoTitleDialog;

import java.util.List;

import rx.functions.Action1;

public class StockShortageDialog extends BaseNoTitleDialog implements View.OnClickListener {

    private List<UnableToBuyModel> unableToBuyModels;
    private Action1<List<UnableToBuyModel>> onConfirmClickAction;

    public StockShortageDialog(final Context context, List<UnableToBuyModel> unableToBuyModels, Action1<List<UnableToBuyModel>> onConfirmClickAction) {
        super(context);
        this.unableToBuyModels = unableToBuyModels;
        this.onConfirmClickAction = onConfirmClickAction;
        setContentView(R.layout.dialog_stock_shortage);
        // Fix dialog size problem
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.dialog_stock_shortage_confirm_btn).setOnClickListener(this);
        TextView messageTextView = (TextView) findViewById(R.id.dialog_stock_shortage_message_tv);
        messageTextView.setText(Settings.checkIsLogIn() ?
                R.string.dialog_stock_not_enough_login_message :
                R.string.dialog_stock_not_enough_not_login_message);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.dialog_stock_shortage_rv);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(context) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                int unableToBuySize = StockShortageDialog.this.unableToBuyModels.size();
                if (unableToBuySize > 3) {
                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                    int displayHeight = 0;
                    for (int i = 0; i < 3; i++) {
                        displayHeight += recyclerView.getChildAt(i).getMeasuredHeight();
                    }

                    params.height = displayHeight;
                    params.height += (int) DensityApi.convertDpToPixel(40, context);
                    recyclerView.setLayoutParams(params);
                }
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new UnableToBuyAdapter(unableToBuyModels));
    }

    @Override
    public void onClick(View view) {
        onConfirmClickAction.call(unableToBuyModels);
        onConfirmClickAction = null;
        dismiss();
    }

    private class UnableToBuyAdapter extends RecyclerView.Adapter<UnableToBuyAdapter.ViewHolder> {

        private List<UnableToBuyModel> unableToBuyEntities;
        private Context context;

        public UnableToBuyAdapter(List<UnableToBuyModel> unableToBuyEntities) {
            this.unableToBuyEntities = unableToBuyEntities;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            context = parent.getContext();
            return new ViewHolder(LayoutInflater.from(context).
                    inflate(R.layout.item_unable_to_buy, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UnableToBuyModel unableToBuy = unableToBuyEntities.get(position);

            Glide.with(context)
                    .load(unableToBuy.getSpec().getStylePic().getUrl())
                    .centerCrop()
                    .placeholder(R.mipmap.img_pre_load_square)
                    .into(holder.imageView);
            holder.productNameTextView.setText(unableToBuy.getName());
            holder.specNameTextView.setText(unableToBuy.getSpec().getStyle());
            holder.quantityTextView.setText(unableToBuy.getQuantityText());
            holder.stockTextView.setText(unableToBuy.getStockText());
            if (position == unableToBuyEntities.size() - 1) {
                holder.bottomLine.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return unableToBuyEntities.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ImageView imageView;
            private final TextView productNameTextView;
            private final TextView specNameTextView;
            private final TextView quantityTextView;
            private final TextView stockTextView;
            private final View bottomLine;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.item_unable_to_buy_iv);
                productNameTextView = (TextView) itemView.findViewById(R.id.item_unable_to_buy_product_name_tv);
                specNameTextView = (TextView) itemView.findViewById(R.id.item_unable_to_buy_spec_name_tv);
                quantityTextView = (TextView) itemView.findViewById(R.id.item_unable_to_buy_quantity_tv);
                stockTextView = (TextView) itemView.findViewById(R.id.item_unable_to_buy_stock_tv);
                bottomLine = itemView.findViewById(R.id.item_unable_to_buy_bottom_line);
            }
        }
    }

}
