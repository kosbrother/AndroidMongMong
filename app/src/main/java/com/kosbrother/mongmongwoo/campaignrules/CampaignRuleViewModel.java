package com.kosbrother.mongmongwoo.campaignrules;

import com.kosbrother.mongmongwoo.entity.camapign.CampaignRuleEntity;
import com.kosbrother.mongmongwoo.utils.DateFormatUtil;

public class CampaignRuleViewModel {

    private int id;
    private String imageUrl;
    private String title;
    private boolean showActivityData;
    private String activityDateText;
    private String description;

    public CampaignRuleViewModel(CampaignRuleEntity campaignRule) {
        id = campaignRule.getId();
        imageUrl = campaignRule.getIcon().getUrl();
        title = campaignRule.getTitle();
        String validUntil = campaignRule.getValidUntil();
        showActivityData = validUntil != null;
        if (showActivityData) {
            activityDateText = DateFormatUtil.parseToYearMonthDay(campaignRule.getCreatedAt())
                    + DateFormatUtil.parseToYearMonthDay(validUntil);
        }
        description = campaignRule.getDescription();
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getActivityDateText() {
        return activityDateText;
    }

    public String getDescription() {
        return description;
    }

    public boolean isShowActivityData() {
        return showActivityData;
    }

    public int getId() {
        return id;
    }
}
