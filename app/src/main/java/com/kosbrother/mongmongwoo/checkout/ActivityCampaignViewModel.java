package com.kosbrother.mongmongwoo.checkout;

import com.kosbrother.mongmongwoo.entity.checkout.Campaign;

public class ActivityCampaignViewModel {
    private static final String TITLE_FORMAT = "(%s，還差NT$%s)";
    private String discountAmountText;
    private String titleText;

    public ActivityCampaignViewModel(Campaign campaign, boolean showLeftToApply) {
        discountAmountText = "-NT$" + campaign.getDiscountAmount();

        String title = campaign.getTitle();
        if (showLeftToApply) {
            titleText = String.format(TITLE_FORMAT, title, campaign.getLeftToApply());
        } else {
            titleText = "(" + title + ")";
        }
    }

    public String getDiscountAmountText() {
        return discountAmountText;
    }

    public String getTitleText() {
        return titleText;
    }
}
