package com.kosbrother.mongmongwoo.myshoppingpoints.campaign;

import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsCampaignsEntity;
import com.kosbrother.mongmongwoo.utils.DateFormatUtil;

public class ShoppingPointsCampaignViewModel {

    public enum CampaignStatus {
        COLLECTED("已領取"), NOTCOLLECTED("未領取"), PROCESSING("進行中");

        private String text;

        CampaignStatus(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private ShoppingPointsCampaignsEntity entity;

    public ShoppingPointsCampaignViewModel(ShoppingPointsCampaignsEntity entity) {
        this.entity = entity;
    }

    public String getAmountText() {
        Integer amount = entity.getAmount();
        if (amount != null && amount != 0 && !entity.getCollected() && !entity.getExpired()) {
            return String.valueOf(amount);
        }
        return "";
    }

    public CampaignStatus getCampaignStatus() {
        if (entity.getCollected()) {
            return CampaignStatus.COLLECTED;
        } else {
            if (entity.getExpired()) {
                return CampaignStatus.NOTCOLLECTED;
            } else {
                return CampaignStatus.PROCESSING;
            }
        }
    }

    public String getTitleText() {
        return entity.getTitle();
    }

    public String getActiveDate() {
        if (entity.getExpired()) {
            return "";
        }
        String validUntil = entity.getValidUntil();
        if (validUntil == null || validUntil.isEmpty()) {
            return "";
        } else {
            String createAt = entity.getCreatedAt();
            return "活動日期：" + DateFormatUtil.parseToYearMonthDay(createAt) + "~" +
                    DateFormatUtil.parseToYearMonthDay(validUntil);
        }
    }

    public String getDescription() {
        return entity.getDescription();
    }

}
