package com.kosbrother.mongmongwoo.checkout;

import com.kosbrother.mongmongwoo.entity.checkout.ShoppingPointCampaign;

public class ShoppingPointCampaignViewModel {
    private static final String TITLE_FORMAT = "(%s，還差NT$%s)";
    private String isAppliedText;
    private String titleText;

    public ShoppingPointCampaignViewModel(ShoppingPointCampaign campaign, boolean showLeftToApply) {
        isAppliedText = campaign.isApplied() ? "資格符合" : "尚未符合";

        String title = campaign.getTitle();
        if (showLeftToApply) {
            titleText = String.format(TITLE_FORMAT, title, campaign.getLeftToApply());
        } else {
            titleText = "(" + title + ")";
        }
    }

    public String getIsAppliedText() {
        return isAppliedText;
    }

    public String getTitleText() {
        return titleText;
    }
}
