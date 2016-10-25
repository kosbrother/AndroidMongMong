package com.kosbrother.mongmongwoo.checkout;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.BR;
import com.kosbrother.mongmongwoo.R;

import java.util.List;

public class ActivityCampaignAdapter extends RecyclerView.Adapter<ActivityCampaignAdapter.ViewHolder> {
    private List<ActivityCampaignViewModel> campaigns;
    private Context context;

    public ActivityCampaignAdapter(List<ActivityCampaignViewModel> campaigns) {
        this.campaigns = campaigns;
    }

    @Override
    public ActivityCampaignAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_activity_campaign, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(context, campaigns.get(position));
    }

    @Override
    public int getItemCount() {
        return campaigns.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(Context context, ActivityCampaignViewModel campaign) {
            binding.setVariable(BR.campaignViewModel, campaign);
            View root = binding.getRoot();
            binding.executePendingBindings();
            TextView titleTextView = (TextView) root.findViewById(R.id.item_order_activity_campaign_title_tv);
            titleTextView.setText(CampaignTextUtil.getCampaignColorSpannable(context, titleTextView.getText()));
        }

    }
}
