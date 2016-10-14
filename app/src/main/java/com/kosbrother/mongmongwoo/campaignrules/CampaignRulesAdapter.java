package com.kosbrother.mongmongwoo.campaignrules;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kosbrother.mongmongwoo.BR;
import com.kosbrother.mongmongwoo.R;

import java.util.List;

public class CampaignRulesAdapter extends RecyclerView.Adapter<CampaignRulesAdapter.ViewHolder> {

    private List<CampaignRuleViewModel> campaignRuleViewModels;
    private CampaignRulesActivity campaignRulesActivity;

    public CampaignRulesAdapter(List<CampaignRuleViewModel> campaignRuleViewModels,
                                CampaignRulesActivity campaignRulesActivity) {
        this.campaignRuleViewModels = campaignRuleViewModels;
        this.campaignRulesActivity = campaignRulesActivity;
    }

    @Override
    public CampaignRulesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_campaign_rule, parent, false));
    }

    @Override
    public void onBindViewHolder(CampaignRulesAdapter.ViewHolder holder, int position) {
        holder.bind(campaignRuleViewModels.get(position), campaignRulesActivity);
    }

    @Override
    public int getItemCount() {
        return campaignRuleViewModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void bind(CampaignRuleViewModel campaignRuleViewModel, CampaignRulesActivity campaignRulesActivity) {
            binding.setVariable(BR.campaignRule, campaignRuleViewModel);
            binding.setVariable(BR.onClickListener, campaignRulesActivity);
            binding.executePendingBindings();
        }
    }
}
