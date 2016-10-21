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

class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private final List<FavoriteViewModel> viewModels;
    private final FavoriteListener listener;

    FavoriteAdapter(List<FavoriteViewModel> viewModels, FavoriteListener listener) {
        this.viewModels = viewModels;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false));
    }

    @Override
    public void onBindViewHolder(FavoriteAdapter.ViewHolder holder, int position) {
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

        public void bind(FavoriteViewModel favoriteViewModel, FavoriteListener listener) {
            binding.setVariable(BR.favoriteViewModel, favoriteViewModel);
            binding.setVariable(BR.favoriteListener, listener);
            binding.executePendingBindings();
        }
    }
}
