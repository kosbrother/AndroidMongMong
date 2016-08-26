package com.kosbrother.mongmongwoo.myshoppingpoints.detail;

import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointRecordsEntity;
import com.kosbrother.mongmongwoo.utils.DateFormatUtil;

public class ShoppingPointsRecordsViewModel {
    private ShoppingPointRecordsEntity entity;

    public ShoppingPointsRecordsViewModel(ShoppingPointRecordsEntity entity) {
        this.entity = entity;
    }

    public String getCreateAtText() {
        return DateFormatUtil.parseToYearMonthDay(entity.getCreatedAt());
    }

    public String getOrderIdText() {
        Integer orderId = entity.getOrderId();
        if (orderId == null || orderId == 0) {
            return "－";
        }
        return "訂單編號" + orderId;
    }

    public String getAmountText() {
        int amount = entity.getAmount();
        if (amount >= 0) {
            return "+$" + amount;
        } else {
            return "-$" + Math.abs(amount);
        }
    }

    public String getBalanceText() {
        return "$" + entity.getBalance();
    }

    public boolean isAmountPositive() {
        return entity.getAmount() >= 0;
    }
}
