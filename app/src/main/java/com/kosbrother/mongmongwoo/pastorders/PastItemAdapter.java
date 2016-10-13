package com.kosbrother.mongmongwoo.pastorders;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kosbrother.mongmongwoo.BR;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.common.ProductViewModel;
import com.kosbrother.mongmongwoo.entity.pastorder.PastItem;

import java.util.List;

public class PastItemAdapter extends RecyclerView.Adapter<PastItemAdapter.ViewHolder> {

    private final List<PastItem> pastItems;

    public PastItemAdapter(List<PastItem> pastItems) {
        this.pastItems = pastItems;
    }

    @Override
    public PastItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail_product, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PastItem item = pastItems.get(position);

        holder.bind(item.getProductViewModel());
    }

    @Override
    public int getItemCount() {
        return pastItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(ProductViewModel productViewModel) {
            binding.setVariable(BR.productViewModel, productViewModel);
        }

    }
}
