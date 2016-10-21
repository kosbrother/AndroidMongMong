package com.kosbrother.mongmongwoo.mycollect;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kosbrother.mongmongwoo.BR;
import com.kosbrother.mongmongwoo.R;

import java.util.List;

class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {
    private final List<WishListViewModel> viewModels;
    private final WishListListener listener;

    WishListAdapter(List<WishListViewModel> viewModels, WishListListener listener) {
        this.viewModels = viewModels;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wish_list, parent, false));
    }

    @Override
    public void onBindViewHolder(WishListAdapter.ViewHolder holder, int position) {
        holder.bind(viewModels.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(WishListViewModel viewModel, WishListListener listener) {
            binding.setVariable(BR.wishListViewModel, viewModel);
            binding.setVariable(BR.wishListListener, listener);
            binding.executePendingBindings();
        }
    }
}
