package com.kosbrother.mongmongwoo.myshoppingpoints.campaign;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;

import java.util.List;

public class ShoppingPointsCampaignAdapter extends RecyclerView.Adapter<ShoppingPointsCampaignAdapter.ViewHolder> {

    private List<ShoppingPointsCampaignViewModel> viewModels;
    private Context context;

    public ShoppingPointsCampaignAdapter(List<ShoppingPointsCampaignViewModel> viewModels) {
        this.viewModels = viewModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_shopping_points_campaign, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ShoppingPointsCampaignViewModel viewModel = viewModels.get(position);
        ShoppingPointsCampaignViewModel.CampaignStatus campaignStatus = viewModel.getCampaignStatus();

        setAmountImageView(campaignStatus, holder.amountImageView);
        setStatusTextViewTextColor(campaignStatus, holder.statusTextView);
        setTextView(campaignStatus.getText(), holder.statusTextView);
        setTextView(viewModel.getAmountText(), holder.amountTextView);
        setTextView(viewModel.getTitleText(), holder.titleTextView);
        setTextView(viewModel.getActiveDate(), holder.activeDateTextView);
        setTextView(viewModel.getDescription(), holder.descriptionTextView);
    }

    private void setAmountImageView(ShoppingPointsCampaignViewModel.CampaignStatus campaignStatus, ImageView amountImageView) {
        switch (campaignStatus) {
            case COLLECTED:
                amountImageView.setImageResource(R.mipmap.ic_money_collected);
                break;
            case NOTCOLLECTED:
                amountImageView.setImageResource(R.mipmap.ic_money_not_collected);
                break;
            case PROCESSING:
                amountImageView.setImageResource(R.mipmap.ic_money_processing);
                break;
            default:
                break;
        }
    }

    private void setStatusTextViewTextColor(ShoppingPointsCampaignViewModel.CampaignStatus campaignStatus, TextView statusTextView) {
        switch (campaignStatus) {
            case COLLECTED:
            case NOTCOLLECTED:
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.grey_text_999999));
                break;
            case PROCESSING:
                statusTextView.setTextColor(ContextCompat.getColor(context, R.color.green_text));
                break;
            default:
                break;
        }
    }

    private void setTextView(String activeDate, TextView activeDateTextView) {
        if (activeDate == null || activeDate.isEmpty()) {
            activeDateTextView.setVisibility(View.GONE);
        } else {
            activeDateTextView.setText(activeDate);
            activeDateTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return viewModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView amountImageView;
        private final TextView amountTextView;
        private final TextView titleTextView;
        private final TextView activeDateTextView;
        private final TextView descriptionTextView;
        private final TextView statusTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            amountImageView = (ImageView) itemView.findViewById(R.id.item_shopping_points_campaign_amount_iv);
            amountTextView = (TextView) itemView.findViewById(R.id.item_shopping_points_campaign_amount_tv);
            titleTextView = (TextView) itemView.findViewById(R.id.item_shopping_points_campaign_title_tv);
            activeDateTextView = (TextView) itemView.findViewById(R.id.item_shopping_points_campaign_active_date_tv);
            descriptionTextView = (TextView) itemView.findViewById(R.id.item_shopping_points_campaign_description_tv);
            statusTextView = (TextView) itemView.findViewById(R.id.item_shopping_points_campaign_status_tv);
        }
    }
}
